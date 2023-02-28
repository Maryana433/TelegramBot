package by.maryana.service.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.dto.MailParams;
import by.maryana.entity.AppUser;
import by.maryana.service.AppUserService;
import by.maryana.service.enums.UserState;
import by.maryana.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Autowired
    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if(appUser.getIsActive()){
            return "You are already registered";
        }else if(appUser.getEmail()!=null){
            return "Please check your mailbox [ "+appUser.getEmail() +" ]. " +
                    "Follow link in email to confirm registration ";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);

        return "Please enter your email address : ";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try{
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Please, enter email address";
        }
        Optional<AppUser> userOptional = appUserDAO.findByEmail(email);
        if(userOptional.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            String userId = cryptoTool.encode(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailService(userId, email);
            if(response.getStatusCode()!= HttpStatus.OK){
                String message = String.format("Sending email to your email address [ %s ] was failed.", email);
                log.error(message);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return message;
            }

            return "Email has been sent to your email address. Please click on link to confirm registration. ";
        }else{
            return "This email address ia already registered.";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams,httpHeaders);

        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);


    }
}
