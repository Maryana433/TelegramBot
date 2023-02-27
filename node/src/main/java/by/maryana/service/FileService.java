package by.maryana.service;

import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long fileId, LinkType linkType);
}
