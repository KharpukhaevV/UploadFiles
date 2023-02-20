import axios from "axios"

const BASE_URL = "http://localhost:8080/file"

class FileService {

    uploadImage(fileFormData){
        return axios.post(BASE_URL +'/upload', fileFormData);
    }

    updateTree() {
        return axios.get(BASE_URL + '/upload');
    }

    createDir(formData) {
        return axios.post(BASE_URL + '/create_dir', formData)
    }

    deleteDir(formData) {
        return axios.post(BASE_URL + '/delete_dir', formData)
    }
}

export default new FileService();