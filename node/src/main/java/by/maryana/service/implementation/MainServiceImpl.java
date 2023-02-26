package by.maryana.service.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.dao.RawDataDAO;
import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.entity.AppUser;
import by.maryana.entity.RawData;
import by.maryana.exceptions.UploadFileException;
import by.maryana.service.FileService;
import by.maryana.service.enums.ServiceCommands;
import by.maryana.service.enums.UserState;
import by.maryana.service.MainService;
import by.maryana.service.ProduceService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static by.maryana.service.enums.UserState.*;
import static by.maryana.service.enums.ServiceCommands.*;

@Log4j
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProduceService produceService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    @Autowired
    public MainServiceImpl(RawDataDAO rawDataDAO, ProduceService produceService, AppUserDAO appUserDAO, FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.produceService = produceService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }


    @Override
    public void processTextMessage(Update update) {

        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";

        ServiceCommands serviceCommand = ServiceCommands.fromValue(text);
        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        }else if(BASIC_STATE.equals(userState)){
            // if our state is Basic - app is waiting for service commands
            output = processServiceCommand(appUser, text);
        }else if(WAIT_FOR_EMAIL_STATE.equals(userState)){
            // if our state is WAIT - app is waiting for users email
            //TODO add process email
        }else{
           log.error("Unknown user state : " + userState);
            output = "Unknown error. Try again.";
        }

        String chatId = update.getMessage().getChatId().toString();
        sendAnswer(output, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        String chatId = update.getMessage().getChatId().toString();
        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String answer = "Photo has been uploaded successfully! link do download : ";
            sendAnswer(answer, chatId);
        }catch (UploadFileException e){
            log.error(e);
            String error = "Photo upload failed. Try again later";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        String chatId = update.getMessage().getChatId().toString();
        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppDocument doc = fileService.processDoc(update.getMessage());
            String answer = "Document has been uploaded successfully! link do download : ";
            sendAnswer(answer, chatId);
        }catch (UploadFileException e){
            log.error(e);
            String error = "File upload failed. Try again later";
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(String chatId, AppUser appUser) {
            UserState userState = appUser.getState();
            if(!appUser.getIsActive()){
                String error = "Please register and active your account to download content";
                sendAnswer(error, chatId);
                return true;
            }else if(!BASIC_STATE.equals(userState)){
                // user can load content only if has BASIC stats
                String error = "Cancel execution of running command /cancel to load content";
                sendAnswer(error, chatId);
                return true;
            }

            return false;
    }

    private void sendAnswer(String output, String chatId) {
        SendMessage sendMessage =  new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        produceService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if(REGISTRATION.equals(cmd)){
            //TODO
            return "Temporarily unavailable";
        }else if(HELP.equals(cmd)){
            return help();
        }else if(START.equals(cmd)){
            return "Hello! List of available command - /help";
        }else{
            return "Unknown command! List of available command - /help";
        }
    }

    private String help() {
        return "List of available commands:\n"
                + "/cancel -  canceling the execution of the current command \n"
                + "/registration - registration of user";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Command was canceled";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .eventFromTelegram(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update){
            User telegramUser = update.getMessage().getFrom();
            AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
            if(persistentAppUser == null){
                AppUser transientAppUser = AppUser.builder()
                        .telegramUserId(telegramUser.getId())
                        .userName(telegramUser.getUserName())
                        .firstName(telegramUser.getFirstName())
                        .lastName(telegramUser.getLastName())
                        // TODO change this value default value after adding registration
                        .isActive(true)
                        .state(BASIC_STATE)
                        .build();

                return appUserDAO.save(transientAppUser);
            }

            return persistentAppUser;
    }
}
