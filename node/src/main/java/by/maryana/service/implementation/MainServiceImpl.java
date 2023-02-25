package by.maryana.service.implementation;

import by.maryana.dao.RawDataDAO;
import by.maryana.entities.RawData;
import by.maryana.service.MainService;
import by.maryana.service.ProduceService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProduceService produceService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProduceService produceService) {
        this.rawDataDAO = rawDataDAO;
        this.produceService = produceService;
    }


    @Override
    public void processTextMessage(Update update) {
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
}
