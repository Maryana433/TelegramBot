package by.maryana.service;

import org.telegram.telegrambots.meta.api.objects.Update;

// read messages from broker
public interface ConsumerService {

    void consumeTextMessageUpdates(Update update);
    void consumeDocMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);

}
