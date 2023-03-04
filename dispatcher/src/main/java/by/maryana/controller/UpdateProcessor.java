package by.maryana.controller;

import by.maryana.service.UpdateProducer;
import by.maryana.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.maryana.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateProcessor {

    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    @Autowired
    public UpdateProcessor(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }

        if(update.hasMessage()){
            distributeMessageByType(update);
        }else{
            log.error("Received unsupported message type " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if(message.hasText()){
            processTextMessage(update);
        }else if(message.hasDocument()){
            processDocumentMessage(update);
        }else if(message.hasPhoto()){
            processPhotoMessage(update);
        }else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils
                .generateAnswerMessage(update,"Unsupported message type");
        setView(sendMessage);
    }

    // send message to Telegram
    private void processTextMessage(Update update) {
        log.debug("Process Text Message");

        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);

//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(update.getMessage().getChatId().toString());
//        sendMessage.setText("We get your message - [ " + update.getMessage().getText() + " ]");
//
//        try {
//            telegramBot.execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }


    private void setFileReceivedView(Update update) {
        SendMessage sendMessage = messageUtils
                .generateAnswerMessage(update,"File wa received! Wait ...");
        setView(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileReceivedView(update);
    }

    private void processDocumentMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileReceivedView(update);
    }


    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
