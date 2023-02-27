package by.maryana.service;

import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
