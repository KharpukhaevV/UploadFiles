import React from "react";
import { useState } from "react";

function Directory ({ files, handlePath }) {
    const [isExpanded, toggleExpanded] = useState(false);

    const handleClick = () => {
        toggleExpanded(!isExpanded);
        handlePath(files.location)
    };

    if (files.type === 'folder') {
        return (
            <div className="folder">
                <span>{!isExpanded ? '📁 ' : '📂 '}</span>
                <span className="folder-title" onClick={handleClick}>{files.name}</span>
                <hr/>
                {
                    isExpanded && files.items.map((items) => <Directory handlePath={handlePath} files={items} />)
                }
            </div>
        )
    }
    return (
        <>
            <span className="file-name">📄 {files.name}</span><br />
            <hr/>
        </>
    )
}

export default Directory;