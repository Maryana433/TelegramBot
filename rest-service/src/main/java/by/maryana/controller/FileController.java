package by.maryana.controller;

import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.entity.BinaryContent;
import by.maryana.service.FileService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@Log4j
public class FileController {


    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id){
        AppDocument appDocument = fileService.getDocument(id);
        // TODO add ControllerAdvice
        if(appDocument == null)
            return ResponseEntity.badRequest().build();

        BinaryContent content = appDocument.getBinaryContent();
        FileSystemResource file = fileService.getFileSystemResource(content);

        if(file == null)
            return ResponseEntity.internalServerError().build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appDocument.getMimType()))
                // to load file, not open
                .header("Content-disposition", "attachment; filename=" + appDocument.getDocName())
                .body(file);

    }

    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id){
        AppPhoto appPhoto = fileService.getPhoto(id);
        // TODO add ControllerAdvice
        if(appPhoto == null)
            return ResponseEntity.badRequest().build();

        BinaryContent content = appPhoto.getBinaryContent();
        FileSystemResource file = fileService.getFileSystemResource(content);

        if(file == null)
            return ResponseEntity.internalServerError().build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                // to load file, not open
                .header("Content-disposition", "attachment;")
                .body(file);
    }

}
