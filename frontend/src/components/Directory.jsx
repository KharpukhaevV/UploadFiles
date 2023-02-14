import React from "react";
import { useState } from "react";

const Directory = ({ files, handlePath }) => {
    const [isExpanded, toggleExpanded] = useState(false);


    if (files.type === 'folder') {
        return (
            <div className="folder">
                <h2 className="folder-title" onClick={() => toggleExpanded(!isExpanded)} onChange={() => handlePath(files.location)}>{files.name}</h2><br />
                {
                    isExpanded && files.items.map((items) => <Directory handlePath={handlePath} files={items} />)
                }
                <button onClick={() => handlePath(files.location)}>Выбрать</button>
            </div>
        )
    }
    return (
        <>
            <h3 className="file-name">{files.name}</h3><br />
        </>
    )
}

export default Directory;