import {useParams, Link} from 'react-router-dom';
import FileExplorer from "./FileExplorer";
import catalog from "../static/catalog.json"
import React from 'react';

function Folder() {
    const location = '/' + useParams()['*'];
    const folder = findFolder(catalog, location);
    if (folder == null) {
        return <div>Folder not found</div>;
    }

    return (
        <div>
            <FileExplorer item={folder}/>
        </div>
    );
}

function findFolder(data, location) {
    if (data.location === location) {
        return data;
    }

    for (const item of data.items) {
        if (item.type === 'folder') {
            const folder = findFolder(item, location);
            if (folder) {
                return folder;
            }
        }
    }

    return null;
}

export default Folder;