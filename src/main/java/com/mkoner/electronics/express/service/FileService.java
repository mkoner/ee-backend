package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.exceptions.FileNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    File saveFile(MultipartFile file) throws Exception;
    List<File> saveFiles(MultipartFile[] files) throws Exception;
    File getFileById (String fileId) throws FileNotFoundException;
    List<File> getAllFiles();
    public byte[] downloadFile(String id) throws IOException;
    Void deleteFile(String fileId) throws FileNotFoundException;
}
