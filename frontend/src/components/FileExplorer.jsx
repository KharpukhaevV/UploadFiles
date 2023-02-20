import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import FileService from "../services/FileService";
import {Modal} from 'react-bootstrap';
import ReactModal from 'react-modal';
import {Spinner} from 'react-bootstrap';

function FileExplorer({item}) {
    const [message, setMessage] = useState("");
    const [files, setFiles] = useState(null);
    const [fileUploaded, setFileUploaded] = useState(null);
    const [uploadError, setUploadError] = useState(false);
    const [dirName, setDirName] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    function handleDragOver(e) {
        e.preventDefault();
        e.stopPropagation();
        const target = e.target
        if (target.classList.contains('drag-and-drop')) {
            target.classList.add('dragover');
        }
    }

    function handleDragLeave(e) {
        e.preventDefault();
        e.stopPropagation();
        const target = e.target
        if (target.classList.contains('drag-and-drop')) {
            target.classList.remove('dragover');
        }
    }


    function handleDrop(e) {
        e.preventDefault();
        e.stopPropagation();
        e.target.classList.remove("dragover");
        setMessage('')
        setFileUploaded(false);
        setDirName('');
        setFiles(null);
        const items = e.dataTransfer.items;
        const fileList = [];
        let folderName = ''

        if (items && items.length > 0) {
            setIsLoading(true);
            setShowModal(true);
            if (items.length > 0) {
                const firstItem = items[0];
                const entry = firstItem.webkitGetAsEntry();
                if (entry != null) {
                    if (entry.isDirectory) {
                        folderName = entry.name;
                    }
                }
            }
            console.log(folderName)

            function traverseFileTree(item, path) {
                return new Promise((resolve, reject) => {
                    if (item.isFile) {
                        item.file((file) => {
                            fileList.push(file);
                            resolve();
                        }, reject);
                    } else if (item.isDirectory) {
                        const dirReader = item.createReader();
                        dirReader.readEntries((entries) => {
                            const promises = [];
                            for (let i = 0; i < entries.length; i++) {
                                promises.push(traverseFileTree(entries[i], path + item.name + "/"));
                            }
                            Promise.all(promises)
                                .then(() => resolve())
                                .catch(reject);
                        }, reject);
                    }
                });
            }

            const promises = [];
            for (let i = 0; i < items.length; i++) {
                const item = items[i].webkitGetAsEntry();
                if (item) {
                    promises.push(traverseFileTree(item));
                }
            }
            console.log(fileList)
            Promise.all(promises)
                .then(() => {
                    setFiles(fileList);
                    setDirName(folderName);
                })
                .catch((error) => {
                    console.error(error);
                });
            onUpload();
        } else {
            setIsLoading(false);
            setUploadError(true);
            setMessage("File list is empty!")
        }
    }


    function onUpload() {
        const formData = new FormData();

        if (files && files.length > 0) {
            for (const key of Object.keys(files)) {
                formData.append('files', files[key]);
            }
            formData.append('path', decodeURI(window.location.pathname.split('folder')[1]))
            formData.append('dirName', dirName)

            FileService.uploadImage(formData).then((response) => {
                console.log(response.data);
                setIsLoading(false);
                setFileUploaded(true);
                setMessage(response.data);
            }).catch(error => {
                console.log(error);
            });
        } else {
            setIsLoading(false);
            setUploadError(true);
            setMessage("Files is not present");
        }
    }

    function createDir() {
        const formData = new FormData();
        const path = decodeURI(window.location.pathname.split('folder')[1]);
        let dirName = prompt('Введите название папки', 'Новая папка');
        formData.append('dirName', dirName);
        formData.append('path', path);
        FileService.createDir(formData);
    }

    function deleteDir(dirName) {
        const msg = "Действительно хотите удалить " + dirName + " и все вложенные файлы?";
        let result = window.confirm(msg);
        if (result) {
            const formData = new FormData();
            const path = decodeURI(window.location.pathname.split('folder')[1]);
            formData.append('dirName', dirName);
            formData.append('path', path);
            FileService.deleteDir(formData);
        }
    }

    const backButtonPath = window.location.pathname.split('/').slice(0, -1).join('/');

    if (item == null) {
        return <div><h3>Каталог не загружен</h3></div>
    }
    if (item.type === 'folder') {
        return (
            <div className='row file-explorer'
                 onDrop={handleDrop}
                 onDragOver={handleDragOver}
                 onDragLeave={handleDragLeave}>
                {showModal && (
                    <ReactModal
                        isOpen={true}
                        ariaHideApp={false}
                        className="modal-container"
                        overlayClassName="modal-overlay">
                        <Modal.Body style={{ textAlign: "center" }}>
                            {isLoading && (
                                <div>
                                    <Spinner animation="border" />
                                    <div>Файлы загружаются на сервер...</div>
                                </div>
                            )}
                            {fileUploaded || uploadError ? (
                                <div>
                                    <div>{message}</div>
                                    <button
                                        className="btn btn-outline-success btn-sm"
                                        onClick={() => {
                                            setShowModal(false);
                                        }}
                                    >
                                        Ок
                                    </button>
                                </div>
                            ) : (
                                <div></div>
                            )}
                        </Modal.Body>
                    </ReactModal>
                )}

                <div className='col-12'>
                    <div className="d-flex justify-content-between align-items-center mb-2">
                        <div>
                            {!window.location.pathname.endsWith('catalogdata') && !window.location.pathname.endsWith('/') && (
                                <Link to={`${backButtonPath}`}>
                                    <button className="btn btn-outline-secondary btn-sm">Назад</button>
                                </Link>)}
                        </div>
                        <div>
                            {!window.location.pathname.endsWith('catalogdata') && !window.location.pathname.endsWith('/') && (
                                <span
                                    className="path">{decodeURI(window.location.pathname.split('catalogdata')[1])}</span>)}
                        </div>
                        <div>
                            <button className="btn btn-outline-success btn-sm" onClick={createDir}>Создать папку
                            </button>
                        </div>
                    </div>
                    <hr/>
                    {!window.location.pathname.endsWith('catalogdata') && !window.location.pathname.endsWith('/') ? (
                        <div className="drag-and-drop">Ператащите папку в эту область для загрузки</div>) : (
                        <div></div>)}
                    <div className="list-group">
                        {item.items.sort((a, b) => a.name.localeCompare(b.name)).map(subItem => (
                            subItem.type === 'folder' ?
                                <div className='d-flex list-group-item list-group-item-action'>
                                    <Link to={`/folder${subItem.location}`}
                                          style={{textDecoration: "none"}}>
                                        <div key={subItem.name} className='d-flex'>
                                            <div className='folder-icon'></div>
                                            <div className='folder-name'> {subItem.name}</div>
                                        </div>
                                    </Link>
                                    <button className="btn btn-outline-secondary btn-sm delete"
                                            onClick={() => deleteDir(subItem.name)}>Удалить
                                    </button>
                                </div> :
                                <div key={subItem.name} className='list-group-item'>
                                    <div className='d-flex'>
                                        {subItem.name.toLowerCase().endsWith('.xls') || subItem.name.toLowerCase().endsWith('xlsx') || subItem.name.toLowerCase().endsWith('ods') ?
                                            <div className='file-xls'></div> :
                                            subItem.name.toLowerCase().endsWith('.doc') || subItem.name.toLowerCase().endsWith('docx') || subItem.name.toLowerCase().endsWith('txt') ?
                                                <div className='file-doc'></div> :
                                                subItem.name.toLowerCase().endsWith('.jpg') || subItem.name.toLowerCase().endsWith('jpeg') ?
                                                    <div className='file-jpg'></div> :
                                                    subItem.name.toLowerCase().endsWith('.pdf') ?
                                                        <div className='file-pdf'></div> :
                                                        <div className='file-icon'></div>
                                        }
                                        <div className='file-name'> {subItem.name}</div>
                                    </div>
                                </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    }
    return null;
}

export default FileExplorer;