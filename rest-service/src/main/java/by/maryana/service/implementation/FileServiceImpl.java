package by.maryana.service.implementation;

import by.maryana.dao.AppDocumentDAO;
import by.maryana.dao.AppPhotoDAO;
import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.entity.BinaryContent;
import by.maryana.service.FileService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    @Autowired
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument getDocument(String docId) {
        // TODO hash
        Long id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        Long id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    // byte array convert to FileSystemResource to send to user
    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try{
            File tmp = File.createTempFile("tempFile", ".bin");
            tmp.deleteOnExit();//delete file after stop program
            FileUtils.writeByteArrayToFile(tmp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(tmp);

        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
