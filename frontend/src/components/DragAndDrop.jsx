import React, { useState } from "react";

function DragAndDrop({ onFilesDrop }) {
    const [message, setMessage] = useState("");

    function handleDragOver(e) {
        e.preventDefault();
        setMessage("...");
    }

    function handleDrop(e) {
        e.preventDefault();
        const files = e.dataTransfer.items;

        if (files[0].webkitGetAsEntry() != null) {
            const dirName = files[0].webkitGetAsEntry().fullPath;
            const fileList = [];

            function traverseFileTree(item, path) {
                path = path || "";
                if (item.isFile) {
                    item.file((file) => {
                        fileList.push(file);
                    });
                } else if (item.isDirectory) {
                    const dirReader = item.createReader();
                    dirReader.readEntries((entries) => {
                        for (let i = 0; i < entries.length; i++) {
                            traverseFileTree(entries[i], path + item.name + "/");
                        }
                    });
                }
            }

            for (let i = 0; i < files.length; i++) {
                const item = files[i].webkitGetAsEntry();
                if (item) {
                    traverseFileTree(item);
                }
            }

            const dirFiles = {
                name: dirName,
                files: fileList,
            };

            setMessage(dirName);
            onFilesDrop(dirFiles);
        }
    }

    return (
        <div
            onDrop={handleDrop}
            onDragOver={handleDragOver}
            style={{ border: "1px solid black", padding: "20px", textAlign: "center" }}
        >
            <p>Перетащите папку для загрузки сюда</p>
            <p>{message}</p>
        </div>
    );
}

export default DragAndDrop;