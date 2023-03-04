package by.maryana.service.implementation;

import by.maryana.controller.UpdateProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateProducerImplTest {

    @Test
    void produceTest(){
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        when(message.getText()).thenReturn("TEXT");
        when(update.getMessage()).thenReturn(message);

        String queue = "QUEUE_NAME";
        new UpdateProducerImpl(rabbitTemplate).produce(queue,update);
        verify(rabbitTemplate).convertAndSend(queue,update);
    }


}
