package ge.luka.filetransferapp.controller;


import jakarta.annotation.PostConstruct;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/user")
public class UserController {

    private final String uploadDir = "C:\\Users\\luka\\OneDrive\\Desktop\\GIO PROJECT\\uploadDir\\";

    @PostConstruct
    public void init()  {
        File directory = new File(uploadDir);
        if(!directory.exists())
            if(!directory.mkdirs())
                throw new RuntimeException("Directory could not be created");
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty())
            return ResponseEntity.badRequest().body("Empty file");
        try{
            String fileName = file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file " + e.getMessage());
        }
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        File file = new File(uploadDir + fileName);
        if(!file.exists() || !file.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try{
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            String contentType = Files.probeContentType(file.toPath());
            if(contentType == null){
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }catch(IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("")
    public String homepage(){
        return "Welcome to the Home Page";
    }





}
