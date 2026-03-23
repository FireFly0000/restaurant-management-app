/*package com.restaurant.commons.utils;

import com.restaurant.commons.core.rpc.storage.FileMetadata;

import java.net.URI;
import java.util.UUID;

public class FileUtils {
    public static String generateObjectKey(FileMetadata fileMetadata){
        return fileMetadata.getEntityId() + "/" + fileMetadata.getFileCategory() + "/" + UUID.randomUUID() + "_" + fileMetadata.getEntityId() + "_" + fileMetadata.getContentLength() + "." + getFileTypeFromFileName(fileMetadata.getFileName());
    }

    public static String getFileTypeFromFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static String extractObjectKeyR2FromUrl(String fileUrl){
        try{
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        }catch (Exception e){
            return null;
        }
    }
}*/
