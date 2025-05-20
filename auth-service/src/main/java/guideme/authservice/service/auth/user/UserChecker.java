package guideme.authservice.service.auth.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import guideme.authservice.infrastructure.dto.user.LoginAccessUser;
import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserChecker {

    private static final String USER_SERVICE_URL = "http://user-service.guideme.svc.cluster.local:9000/api/user/login";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserChecker() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    // user 회원가입 유무에 따라서 정보를 다르게 전달한다.
    public UserInfoChecker getUserInfoCheck(LoginAccessUser accessUser) {
        try {
            Map<String, Object> body = getUserInfoFromUserService(accessUser);
            return generateUserInfoChecker(body);
        } catch (Exception e) {
            throw new IllegalArgumentException("error from getUserInfoCheck", e);
        }
    }

    private Map<String, Object> getUserInfoFromUserService(LoginAccessUser accessUser) {
        Map<String, Object> body = getRequestBody(accessUser);
        if (body == null || Boolean.FALSE.equals(body.get("isSuccess"))) {
            throw new IllegalArgumentException("");
        }
        return body;
    }

    private Map<String, Object> getRequestBody(LoginAccessUser accessUser) {
        String url = USER_SERVICE_URL + "?email=" + accessUser.getEmail() + "&student_id=" + accessUser.getStudentId();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        return response.getBody();
    }

    private UserInfoChecker generateUserInfoChecker(Map<String, Object> body) {
        Object data = body.get("data");
        return objectMapper.convertValue(data, UserInfoChecker.class);
    }
}
