package guideme.authservice.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import guideme.authservice.infrastructure.dto.user.LoginAccessUser;
import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserChecker {

    private static final String USER_SERVICE_URL = "http://user-service/user/email";
    private static final String USER_EMAIL_ID = "user_email_id";
    private static final String STUDENT_ID = "student_id";
    private static final String USER_INFO_URL = "https://api.idp.gistory.me/oauth/userinfo";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserChecker() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public LoginAccessUser getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return getUserInfoAndConvertAccessEntity(entity);
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

    private LoginAccessUser getUserInfoAndConvertAccessEntity(HttpEntity<Void> entity) {
        try {
            Map<String, Object> userInfo = getUserSpecificInfo(entity);
            if (userInfo == null) {
                throw new IllegalArgumentException("");
            }
            return createLoginAccessUser(userInfo);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    private Map<String, Object> getUserSpecificInfo(HttpEntity<Void> entity) {
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    private LoginAccessUser createLoginAccessUser(Map<String, Object> userInfo) {
        String email = (String) userInfo.get(USER_EMAIL_ID);
        String studentId = (String) userInfo.get(STUDENT_ID);
        return new LoginAccessUser(email, studentId);
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
