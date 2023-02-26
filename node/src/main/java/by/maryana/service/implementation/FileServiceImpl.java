package by.maryana.service.implementation;

import by.maryana.dao.AppDocumentDAO;
import by.maryana.dao.AppPhotoDAO;
import by.maryana.dao.BinaryContentDAO;
import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.entity.BinaryContent;
import by.maryana.exceptions.UploadFileException;
import by.maryana.service.FileService;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${telegram.bot.token}")
    private String telegramToken;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;


    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {

        Document telegramDoc = telegramMessage.getDocument();

        // get FilePath of File by FileId
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String>response = getFilePath(fileId);

        if(response.getStatusCode() == HttpStatus.OK){

            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);

            //save AppDocument to DB
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        }else{
            throw new UploadFileException("Bad response from telegram service: "  + response);
        }

    }


    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        // TODO process only 1 photo
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);

        // get FilePath of File by FileId
        String fileId = telegramMessage.getPhoto().get(0).getFileId();
        ResponseEntity<String>response = getFilePath(fileId);

        if(response.getStatusCode() == HttpStatus.OK){

            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);

            //save AppDocument to DB
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        }else{
            throw new UploadFileException("Bad response from telegram service: "  + response);
        }
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {

        String filePath = getFilePathFromResponse(response);

        byte[] fileInByte = downloadFile(filePath);

        // save binary file to DB
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePathFromResponse(ResponseEntity<String> response) {

        // convert Body to JSON
        JSONObject jsonObject = new JSONObject(response.getBody());

        String filePath = String.valueOf(jsonObject
            .getJSONObject("result")
            .getString("file_path"));
        return filePath;
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
            String fullUri = fileStorageUri.replace("{token}", telegramToken)
                    .replace("{filePath}", filePath);
            URL urlObj = null;
            try{
                urlObj = new URL(fullUri);
            } catch (MalformedURLException e) {
                throw new UploadFileException(e);
            }

            try(InputStream is = urlObj.openStream()){
                return is.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                throw new UploadFileException(e);
            }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                telegramToken,
                fileId
        );

    }
}
