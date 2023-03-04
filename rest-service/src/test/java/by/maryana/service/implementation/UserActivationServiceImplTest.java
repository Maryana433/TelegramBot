package by.maryana.service.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.entity.AppUser;
import by.maryana.utils.CryptoTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserActivationServiceImplTest {

    private AppUserDAO appUserDAO;

    private final String salt = "salt";
    private final CryptoTool cryptoTool = new CryptoTool(salt);
    private final Long id = 1L;
    private final Long notId = 2L;
    private AppUser appUser;



    @BeforeEach
    void init(){
        appUserDAO = mock(AppUserDAO.class);
        appUser = mock(AppUser.class);
        when(appUserDAO.findById(id)).thenReturn(Optional.of(appUser));
    }

    @Test
    void shouldReturnFalseIfUserNotExistsInDB(){
        String hash = cryptoTool.encode(notId);

        UserActivationServiceImpl service = new UserActivationServiceImpl(appUserDAO, cryptoTool);
        boolean isActive = service.activation(hash);

        verify(appUserDAO).findById(notId);
        assertFalse(isActive);
    }

    @Test
    void shouldReturnTrueIfUserExistsInDBAndSetActiveAsTrue(){
        String hash = cryptoTool.encode(id);

        UserActivationServiceImpl service = new UserActivationServiceImpl(appUserDAO, cryptoTool);
        boolean isActive = service.activation(hash);

        verify(appUserDAO).findById(id);
        verify(appUser).setIsActive(true);
        verify(appUserDAO).save(appUser);
        assertTrue(isActive);

    }
}
