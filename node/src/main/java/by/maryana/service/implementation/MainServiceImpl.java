package by.maryana.service.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.dao.RawDataDAO;
import by.maryana.entity.AppUser;
import by.maryana.entity.RawData;
import by.maryana.entity.enums.UserState;
import by.maryana.service.MainService;
import by.maryana.service.ProduceService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProduceService produceService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProduceService produceService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.produceService = produceService;
        this.appUserDAO = appUserDAO;
    }


    @Override
    public void processTextMessage(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser appUser = findOrSaveAppUser(telegramUser);

        var message = update.getMessage();
        var sendMessage =  new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hello from NODE");
        produceService.produceAnswer(sendMessage);

        saveRawData(update);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .eventFromTelegram(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUser findOrSaveAppUser(User telegramUser){
            AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
            if(persistentAppUser == null){
                AppUser transientAppUser = AppUser.builder()
                        .telegramUserId(telegramUser.getId())
                        .userName(telegramUser.getUserName())
                        .firstName(telegramUser.getFirstName())
                        .lastName(telegramUser.getLastName())
                        // TODO change this value default value after adding registration
                        .isActive(true)
                        .state(UserState.BASIC_STATE)
                        .build();

                return appUserDAO.save(transientAppUser);
            }

            return persistentAppUser;
    }
}
