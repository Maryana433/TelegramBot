package by.maryana.service;

import by.maryana.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
