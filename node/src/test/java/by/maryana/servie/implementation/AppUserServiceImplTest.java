package by.maryana.servie.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.entity.AppUser;
import by.maryana.service.enums.UserState;
import by.maryana.service.implementation.AppUserServiceImpl;
import by.maryana.utils.CryptoTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.context.TestPropertySource;


import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AppUserServiceImplTest {

    private AppUserDAO appUserDAO;
    private AppUser appUser;
    private final String salt = "salt";
    private final CryptoTool cryptoTool = new CryptoTool(salt);

    @BeforeEach
    void init(){
        appUserDAO = mock(AppUserDAO.class);
        appUser = mock(AppUser.class);
    }

    @Test
    void shouldJustReturnHelpStringIfUserHasAlreadyRegistered(){
        when(appUser.getIsActive()).thenReturn(true);

        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
        service.registerUser(appUser);

        verify(appUser).getIsActive();
        verifyNoMoreInteractions(appUser);
        verifyNoInteractions(appUserDAO);
    }

    @Test
    void shouldJustReturnHelpStringIfUserHasAlreadySendEmail(){
        when(appUser.getIsActive()).thenReturn(false);
        when(appUser.getEmail()).thenReturn("TEST");

        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
        service.registerUser(appUser);

        verify(appUser).getIsActive();
        verify(appUser, atLeastOnce()).getEmail();
        verifyNoMoreInteractions(appUser);
        verifyNoInteractions(appUserDAO);
    }

    @Test
    void shouldSaveUserAndSetStatusIfUserNotRegisteredAndNotSentEmail(){
        when(appUser.getIsActive()).thenReturn(false);
        when(appUser.getEmail()).thenReturn(null);

        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
        service.registerUser(appUser);

        verify(appUser).getIsActive();
        verify(appUser, atLeastOnce()).getEmail();
        verify(appUser).setState(UserState.WAIT_FOR_EMAIL_STATE);
        verify(appUserDAO).save(appUser);
        verifyNoMoreInteractions(appUser);
        verifyNoMoreInteractions(appUserDAO);
    }


    @Test
    void shouldJustReturnHelpStringIfEmailIsNotValid(){
        String notValidEmail = "test";

        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
        service.setEmail(appUser, notValidEmail);

        verifyNoInteractions(appUser);
        verifyNoInteractions(appUserDAO);
    }

// should send POST request - org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://127.0.0.1:8087/mail/send":
//    @Test
//    void shouldSaveUserAndSentEmailIfUserWithThetEmailNotExists(){
//        String email = "test@gmail.com";
//        when(appUserDAO.findByEmail(email)).thenReturn(Optional.empty());
//        when(appUser.getId()).thenReturn(1L);
//        when(appUserDAO.save(appUser)).thenReturn(appUser);
//
//        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
//
//        try {
//            Field f = service.getClass().getDeclaredField("mailServiceUri");
//            f.setAccessible(true);
//            f.set(service, "http://127.0.0.1:8087/mail/send");
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        service.setEmail(appUser, email);
//
//        InOrder inOrder = inOrder(appUserDAO, appUser,appUser,appUserDAO);
//        inOrder.verify(appUserDAO).findByEmail(email);
//        inOrder.verify(appUser).setEmail(email);
//        inOrder.verify(appUser).setState(UserState.BASIC_STATE);
//        inOrder.verify(appUserDAO).save(appUser);
//
//        verifyNoMoreInteractions(appUserDAO);
//        verifyNoMoreInteractions(appUser);
//    }


    @Test
    void shouldJustReturnHelpStringIfEmailIsValidButExistsUserWithWhatEmail(){
        String email = "test@gmail.com";
        when(appUserDAO.findByEmail(email)).thenReturn(Optional.of(appUser));

        AppUserServiceImpl service = new AppUserServiceImpl(appUserDAO, cryptoTool);
        service.setEmail(appUser, email);

        verify(appUserDAO).findByEmail(email);
        verifyNoMoreInteractions(appUserDAO);
        verifyNoInteractions(appUser);
    }
}
