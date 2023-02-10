import React from "react";
import { useDropzone } from "react-dropzone";


export default function Basic(props) {
    const { acceptedFiles, getRootProps, getInputProps } = useDropzone();

    const files = acceptedFiles.map(file => (
        <li key={file.path}>
            {file.path} - {file.size} bytes
        </li>
    ));
    function upload() {
        console.log(files)
    }

    return (
        <div className="row">
            <div className="drag-area">
                <section className="container">
                    <div {...getRootProps({ className: "dropzone" })}>
                        <input {...getInputProps()} />
                        <p>Drag 'n' drop some files here, or click to select files</p>
                    </div>
                    <aside>
                        <ul>{files}</ul>
                    </aside>
                </section>
            </div>
            <div>
                <button type="button" className="btn btn-outline-primary btn-sm btn-block" onClick={upload}>Загрузить</button>
            </div>
        </div>

    );
}

