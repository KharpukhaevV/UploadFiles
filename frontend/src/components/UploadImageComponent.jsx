import React, {Component} from 'react';
import {Navigate} from 'react-router-dom';
import FileService from '../services/FileService';
import Basic from './upload.component';
import {useDropzone} from "react-dropzone";
import Directiry from "./Directiry";
import catalog from "../static/catalog.json"


class UploadImageComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            files: null,
            fileUploaded: null
        }
    }


    onFileChange = (event) => {
        this.setState({
            files: event.target.files
        });
    }


    onUpload = (event) => {
        event.preventDefault();
        const formData = new FormData();

        for (const key of Object.keys(this.state.files)) {
            formData.append('files', this.state.files[key]);
        }

        FileService.uploadImage(formData).then((response) => {
            console.log(response.data);
            this.setState({fileUploaded: true});
        }).catch(error => {
            console.log(error);
        });
    }

    render() {
        if (this.state.fileUploaded) {
            return <Navigate to="/my-images" replace={true}/>;
        }

        return (
            <div className='row'>
                <div className='row'>
                    <div className='card col-md-6 offset-md-3 mt-5'>
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
                <div className='row'>
                    <div className='card col-md-6 offset-md-3 mt-5'>
                        <div className="spacing">
                            <Directiry files={catalog} />
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default UploadImageComponent;