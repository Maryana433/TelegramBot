package by.maryana.service;

import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;


public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}
