package by.maryana.utils;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageUtilsTest {

    static Update update;
    static Message message;
    static final Long chatId = 1L;
    static final String text = "TEXT";

    @BeforeAll
    public static void init(){
        update = mock(Update.class);
        message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
    }

    @Test
    void generateAnswerMessageWithProperTextAndChatId(){
        SendMessage message = new MessageUtils().generateAnswerMessage(update, text);
        assertEquals(chatId.toString(), message.getChatId());
        assertEquals(text, message.getText());

    }

}
