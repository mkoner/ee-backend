package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.FileNotFoundException;
import com.mkoner.electronics.express.repository.FileRepository;
import com.mkoner.electronics.express.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileRepository fileRepository;
    private  final String FOLDER ="/var/test_electronique_express/api/uploads/";
    //private  final String FOLDER ="/uploads";


   /*

    @Override
    public File saveFile(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence "
                        + fileName);
            }

            File file1 = new File();
            file1.setFileName(fileName);
            file1.setFileType(file.getContentType());
            file1.setData(file.getBytes());
            File savedFile = fileRepository.save(file1);
            return savedFile;

        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
    }

    @Override
    public List<File> saveFiles(MultipartFile[] files) throws Exception {
        List<File> savedFiles = new ArrayList<>();
        for(MultipartFile file:files){
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            try {
                if(fileName.contains("..")) {
                    throw  new Exception("Filename contains invalid path sequence "
                            + fileName);
                }

                File file1 = new File();
                file1.setFileName(fileName);
                file1.setFileType(file.getContentType());
                file1.setData(file.getBytes());
                File savedFile = fileRepository.save(file1);
                savedFiles.add(savedFile);

            } catch (Exception e) {
                throw new Exception("Could not save File: " + fileName);
            }
        }
        return savedFiles;
    }

    */

    @Override
    public File saveFile(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
        try {
            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence "
                        + fileName);
            }

            File file1 = new File();
            file1.setFileName(fileName);
            file1.setFileType(file.getContentType());
            file1.setPath(FOLDER+date+file.getOriginalFilename());
            file.transferTo(new java.io.File(file1.getPath()));
            File savedFile = fileRepository.save(file1);
            return savedFile;

        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
    }

    @Override
    public List<File> saveFiles(MultipartFile[] files) throws Exception {
        List<File> savedFiles = new ArrayList<>();
        for(MultipartFile file:files){
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
            try {
                if(fileName.contains("..")) {
                    throw  new Exception("Filename contains invalid path sequence "
                            + fileName);
                }

                File file1 = new File();
                file1.setFileName(fileName);
                file1.setFileType(file.getContentType());
                file1.setPath(FOLDER+date+file.getOriginalFilename());
                file.transferTo(new java.io.File(file1.getPath()));
                File savedFile = fileRepository.save(file1);
                savedFiles.add(savedFile);

            } catch (Exception e) {
                throw new Exception("Could not save File: " + fileName);
            }
        }
        return savedFiles;
    }
    @Override
    public File getFileById(String fileId) throws FileNotFoundException {
        Optional<File> file = fileRepository.findById(fileId);
        if(file.isEmpty())
            throw new FileNotFoundException(ExceptionMessages.FILE_NOT_FOUND);
        return file.get();
    }

    @Override
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    @Override
    public byte[] downloadFile(String id) throws IOException {
        File file = fileRepository.findById(id).get();
        String path = file.getPath();
        byte[] images = Files.readAllBytes(new java.io.File(path).toPath());
        return images;
    }

    @Override
    public Void deleteFile(String fileId) throws FileNotFoundException {
        Optional<File> file = fileRepository.findById(fileId);
        if(file.isEmpty())
            throw new FileNotFoundException(ExceptionMessages.FILE_NOT_FOUND);
        fileRepository.deleteById(fileId);
        return null;
    }
}
