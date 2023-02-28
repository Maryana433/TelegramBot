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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

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
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) throws IOException {

        AppDocument appDocument = fileService.getDocument(id);
        // TODO add ControllerAdvice
        if (appDocument == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ;
    }

        BinaryContent content = appDocument.getBinaryContent();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.parseMediaType(appDocument.getMimType()).toString());
        response.setHeader("Content-disposition", "attachment; filename="+ appDocument.getDocName());

        try{
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(content.getFileAsArrayOfBytes());
            outputStream.close();
        }catch(IOException e){
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response){
        AppPhoto appPhoto = fileService.getPhoto(id);
        // TODO add ControllerAdvice
        if(appPhoto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BinaryContent content = appPhoto.getBinaryContent();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment;");

        try{
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(content.getFileAsArrayOfBytes());
            outputStream.close();
        }catch(IOException e){
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
