package by.maryana.controller;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


// Polling - всегда запрашиваем есть ли данные
// WebHooks - сам телеграмм отправляет данные - лучше, но нужен static IP и ssl sertificate
// - нужно платить денежку
@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    // messages from Bot and to Bot
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        log.debug(message.getText());

        // view
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId().toString());
        response.setText("Hello from bot");
        sendAnswerMessage(response);

    }

    public void sendAnswerMessage(SendMessage message){
        if(message != null){
            try{
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
