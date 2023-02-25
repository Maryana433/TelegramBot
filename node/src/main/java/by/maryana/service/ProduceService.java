package by.maryana.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

// send messages from bot to user
public interface ProduceService {
    void produceAnswer(SendMessage sendMessage);
}
