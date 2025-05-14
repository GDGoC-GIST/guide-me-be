package guideme.authservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Value("${jwt.issuer}")
    private String issuer;

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/info")
    public String info() {
        return issuer;
    }
}
