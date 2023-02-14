import React, {Component} from 'react';
import {Navigate} from 'react-router-dom';
import FileService from '../services/FileService';
import Directory from "./Directory";
import catalog from "../static/catalog.json"


class UploadImageComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            files: null,
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

        FileService.uploadImage(formData).then((response) => {
            console.log(response.data);
            this.setState({fileUploaded: true});
            this.setState({message: response.data})
        }).catch(error => {
            console.log(error);
        });
    }

    render() {
        return (
            <div className='row'>
                <h4>{this.state.path}</h4>
                <div className='row'>
                    {this.state.fileUploaded ?
                        <div className='card col-6'>
                            <h3>{this.state.message}</h3>
                        </div>:
                        <div></div>
                    }
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
                                <div>
                                    <label>Select a file:</label>
                                    <input className='mx-2' id="input-file-upload" type='file' name='file' directory=""
                                           webkitdirectory="" onChange={this.onFileChange} multiple></input>
                                </div>
                                <button className='btn btn-success btn-sm mt-3' type='submit'
                                        disabled={!this.state.files}>Загрузить
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default UploadImageComponent;