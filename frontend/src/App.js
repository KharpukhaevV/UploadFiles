import {Route, Routes} from "react-router-dom";
import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import './App.css';
import HeaderComponent from './components/HeaderComponent';
import UploadImageComponent from './components/UploadImageComponent';
import FileExplorer from "./components/FileExplorer";
import Folder from "./components/Folder";

function App() {
    const [catalog, setCatalog] = useState(null);

    useEffect(() => {
        fetch("http://localhost:8080/file/upload")
            .then((response) => response.json())
            .then((data) => setCatalog(data))
            .catch((error) => console.error(error));
    }, []);

    return (
        <Router>
            <HeaderComponent/>
            <div className='container'>
                <Routes>
                    <Route path='/upload' element={<UploadImageComponent/>}></Route>
                    <Route path="/" element={<FileExplorer item={catalog}/>}/>
                    <Route path="/folder">
                        <Route path="*" element={<Folder/>}/>
                    </Route>
                </Routes>
            </div>
        </Router>
    );
}

export default App;
