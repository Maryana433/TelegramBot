package by.maryana.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


// Polling - всегда запрашиваем есть ли данные
// WebHooks - сам телеграмм отправляет данные - лучше, но нужен static IP и ssl sertificate
// - нужно платить денежку
@Component
@Log4j
public class TelegramBot extends TelegramWebhookBot {

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.uri}")
    private String botUri;

    private UpdateProcessor updateProcessor;

    public TelegramBot(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        updateProcessor.registerBot(this);
        try{
            SetWebhook webhook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(webhook);
        }catch(TelegramApiException e){
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return "/update"; // <bot.uri> + </callback> + </update?
    }

    // TelegramLongPollingBot
//    public void onUpdateReceived(Update update) {
//        updateProcessor.processUpdate(update);
//    }

    public void sendAnswerMessage(SendMessage message){
        if(message != null){
            try{
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

}
