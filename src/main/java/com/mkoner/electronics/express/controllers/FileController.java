package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.exceptions.FileNotFoundException;
import com.mkoner.electronics.express.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<List <File>> uploadFile(@RequestParam("files") MultipartFile[] files) throws Exception {
        return new ResponseEntity<>(fileService.saveFiles(files), HttpStatus.CREATED);

    }

    /*
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws FileNotFoundException {
        File file = null;
        file = fileService.getFileById(fileId);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName()
                                + "\"")
                .body(new ByteArrayResource(file.getData()));
    }

     */

    @GetMapping("/{id}")
    public  ResponseEntity<?> downloadFile(@PathVariable("id") String id) throws IOException, FileNotFoundException {
        byte[] imageData = fileService.downloadFile(id);
        File file = fileService.getFileById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(imageData);
    }

    @GetMapping()
    public List<File> getAllFiles(){
        return fileService.getAllFiles();
    }
}
