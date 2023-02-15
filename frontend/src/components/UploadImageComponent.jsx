import React, {Component} from 'react';
import FileService from '../services/FileService';
import Directory from "./Directory";
import DragAndDrop from "./DragAndDrop";


class UploadImageComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            files: null,
            dirName: '',
            fileUploaded: null,
            path: '',
            tree: null,
            message: ''
        }
    }

    handlePath = (pathValue) => {
        this.setState({path: pathValue})
        this.setState({
            path: pathValue
        })
        console.log(pathValue)
    }

    onFileChange = (event) => {
        console.log(event.target.files)
        this.setState({
            files: event.target.files
        });
    }

    componentDidMount = () => {
        FileService.updateTree().then((response) => {
            this.setState({ tree: response.data });
        });
    }

    onUpload = (event) => {
        event.preventDefault();
        const formData = new FormData();

        for (const key of Object.keys(this.state.files)) {
            formData.append('files', this.state.files[key]);
        }
        formData.append('path', this.state.path)
        formData.append('dirName', this.state.dirName)

        FileService.uploadImage(formData).then((response) => {
            console.log(response.data);
            this.setState({fileUploaded: true});
            this.setState({message: response.data})
        }).catch(error => {
            console.log(error);
        });
    }

    handleDropFiles = (e) => {
        console.log(e.name)
        console.log(e.files)
        this.setState({
            files: e.files,
            dirName: e.name
        });
    }

    render() {
        return (
            <div className='row'>
                <div className='row'>
                    <div className='card col-7'>
                        <div className="spacing">
                            {this.state.tree != null ?
                                <Directory handlePath={this.handlePath} files={this.state.tree} /> :
                                <div><h3>Каталог не загружен</h3></div>
                            }

                        </div>
                    </div>
                    <div className='card col-md-5'>
                        <div className='card-body'>
                            <form onSubmit={this.onUpload}>
                                <span>Папка загрузки: {this.state.path}</span>
                                <hr/>
                                <DragAndDrop onFilesDrop={this.handleDropFiles}/>
                                {!(this.state.files && this.state.path)?
                                    <button className='btn btn-success btn-sm mt-3' type='submit'
                                            disabled={true}>Загрузить
                                    </button> :
                                    <button className='btn btn-success btn-sm mt-3' type='submit'
                                            disabled={false}>Загрузить
                                    </button>
                                }
                            </form>
                            {this.state.fileUploaded ?
                                <h4>{this.state.message}</h4> : <h4></h4>
                            }
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default UploadImageComponent;