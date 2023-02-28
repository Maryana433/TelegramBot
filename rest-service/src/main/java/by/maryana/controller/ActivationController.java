package by.maryana.controller;


import by.maryana.service.UserActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ActivationController {

    private final UserActivationService userActivationService;


    @Autowired
    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String userId){
        boolean isActivate = userActivationService.activation(userId);
        if(isActivate){
            return ResponseEntity.ok().body("Activation your account is completed. You can load documents and photos");
        }

        return ResponseEntity.internalServerError().build();
    }
}
