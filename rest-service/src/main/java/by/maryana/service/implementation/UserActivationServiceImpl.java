package by.maryana.service.implementation;

import by.maryana.dao.AppUserDAO;
import by.maryana.entity.AppUser;
import by.maryana.service.UserActivationService;
import by.maryana.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO userDAO;
    private final CryptoTool cryptoTool;

    @Autowired
    public UserActivationServiceImpl(AppUserDAO userDAO, CryptoTool cryptoTool) {
        this.userDAO = userDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.decode(cryptoUserId);
        Optional<AppUser> userOptional = userDAO.findById(userId);
        if(userOptional.isPresent()){
                AppUser user = userOptional.get();
                user.setIsActive(true);
                userDAO.save(user);
                return true;
        }

        return false;
    }
}
