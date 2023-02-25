package by.maryana.service.implementation;

import by.maryana.service.ProduceService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static by.maryana.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class ProduceServiceImpl implements ProduceService {

    private final RabbitTemplate rabbitTemplate;

    public ProduceServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
            rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
}
