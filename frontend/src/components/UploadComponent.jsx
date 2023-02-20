import React, {Component} from "react";
import FileService from "../services/FileService";
import DragAndDrop from "./DragAndDrop";

class UploadComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            files: null,
            fileUploaded: null,
            tree: null,
            message: '',
            dirName: ''
        }
    }



    onUpload = (event) => {
        event.preventDefault();
        const formData = new FormData();

        for (const key of Object.keys(this.state.files)) {
            formData.append('files', this.state.files[key]);
        }
        formData.append('path', decodeURI(window.location.pathname.split('folder')[1]))
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
        this.setState({
            files: e.files,
            dirName: e.name
        });
    }

    render() {
        console.log(decodeURI(window.location.pathname.split('folder')[1]))
        console.log(decodeURI(window.location.pathname.split('/')[window.location.pathname.split('/').length - 1]))
        return (
            <div className='col-5'>
                <form onSubmit={this.onUpload}>
                    <hr/>
                    <DragAndDrop onFilesDrop={this.handleDropFiles}/>
                    <button className='btn btn-success btn-sm mt-3' type='submit'
                            disabled={!this.state.files}>Загрузить
                    </button>
                </form>
                {this.state.fileUploaded ?
                    <h4>{this.state.message}</h4> : <h4></h4>
                }
            </div>
        )
    }
}

export default UploadComponent;