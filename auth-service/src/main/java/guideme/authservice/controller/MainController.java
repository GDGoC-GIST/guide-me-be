package guideme.authservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "probe",
        description = "k3s cluster pod probe 확인용 요청"
)
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
