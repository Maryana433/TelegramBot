package by.maryana.service.implementation;

import by.maryana.controller.UpdateProcessor;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AnswerConsumerImplTest {

    @Test
    void consumeMessageTest(){
        UpdateProcessor updateProcessor = mock(UpdateProcessor.class);
        SendMessage sendMessage = new SendMessage();
        new AnswerConsumerImpl(updateProcessor).consume(sendMessage);
        verify(updateProcessor).setView(sendMessage);
    }


}
