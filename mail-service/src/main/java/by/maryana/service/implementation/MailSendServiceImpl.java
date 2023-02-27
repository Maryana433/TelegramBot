package by.maryana.service.implementation;

import by.maryana.dto.MailParams;
import by.maryana.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSendServiceImpl implements MailSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    private final JavaMailSender mailSender;

    public MailSendServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(MailParams mailParams) {
        String mailSubject = "Activate account in telegram bot";
        String messageBody = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(mailSubject);
        message.setText(messageBody);

        mailSender.send(message);
    }

    private String getActivationMailBody(String id) {

        String msg = String.format("To complete registration click on link :\n%s",
                activationServiceUri);
        return msg.replace("{id}", id);
    }
}
