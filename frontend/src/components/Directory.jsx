import React from "react";
import { useState } from "react";

const Directory = ({ files, handlePath }) => {
    const [isExpanded, toggleExpanded] = useState(false);


    if (files.type === 'folder') {
        return (
            <div className="folder">
                <h2 className="folder-title" onClick={() => toggleExpanded(!isExpanded)} onChange={() => handlePath(files.location)}>{files.name}</h2>
                <button onClick={() => handlePath(files.location)}>Выбрать</button>
                <hr/>
                {
                    isExpanded && files.items.map((items) => <Directory handlePath={handlePath} files={items} />)
                }
            </div>
        )
    }
    return (
        <>
            <h3 className="file-name">{files.name}</h3><br />
            <hr/>
        </>
    )
}

export default Directory;