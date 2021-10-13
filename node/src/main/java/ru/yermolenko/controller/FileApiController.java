package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yermolenko.model.Document;
import ru.yermolenko.model.Photo;
import ru.yermolenko.service.FileService;

import java.io.File;
import java.io.IOException;

@RestController
@Log4j
@RequestMapping("/api/file")
public class FileApiController {
    private final FileService fileService;

    public FileApiController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get_doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        Document doc = fileService.getDocument(id);
        if (doc == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            File temp = File.createTempFile("myTempFile", ".bin");
            FileUtils.writeByteArrayToFile(temp, doc.getFileAsArrayOfBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(doc.getMimeType()))
                    .header("Content-disposition",
                            "attachment; filename=" +
                                    doc.getDocName())
                    .body(new FileSystemResource(temp));
        } catch (IOException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get_photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        Photo photo = fileService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            File temp = File.createTempFile("myTempFile", ".bin");
            FileUtils.writeByteArrayToFile(temp, photo.getFileAsArrayOfBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header("Content-disposition","attachment;")
                    .body(new FileSystemResource(temp));
        } catch (IOException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }
}
