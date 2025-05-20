package guideme.authservice.service.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import guideme.authservice.infrastructure.dto.user.LoginAccessUser;
import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * UserChecker 단위 테스트
 */
class UserCheckerTest {

    private static final String EXPECTED_JSON = """
        {
          "data": {
            "userId": "63b4ecab-856f-4277-8a1d-6ebe5afc4b77",
            "email": "test",
            "userRole": "PENDING",
            "studentId": "test",
            "nickname": null,
            "semester": null
          },
          "status": 200,
          "timeStamp": "2025-05-20T07:55:38.484500Z",
          "success": true
        }
    """;

    private static final String EXPECTED_JSON_WITH_NICKNAME = """
        {
          "data": {
            "userId": "63b4ecab-856f-4277-8a1d-6ebe5afc4b77",
            "email": "test",
            "userRole": "ABLE",
            "studentId": "test",
            "nickname": "tester",
            "semester": 1
          },
          "status": 200,
          "timeStamp": "2025-05-20T07:55:38.484500Z",
          "success": true
        }
    """;


    private UserChecker userChecker;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 실제 RestTemplate 대신 테스트 전용 RestTemplate 준비
        RestTemplate restTemplate = new RestTemplate();

        // UserChecker 인스턴스 생성 후 RestTemplate 필드 교체
        userChecker = new UserChecker();
        ReflectionTestUtils.setField(userChecker, "restTemplate", restTemplate);

        // Mock 서버 준비
        mockServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserInfoCheck_success() throws Exception {
        // given
        LoginAccessUser accessUser = new LoginAccessUser("test", "test");

        String expectedUrl =
                "http://user-service.guideme.svc.cluster.local:9000/api/user/login"
                        + "?email=test&student_id=test";

        // Mock 서버가 기대하는 요청과 응답 정의
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(EXPECTED_JSON, MediaType.APPLICATION_JSON));

        // when
        UserInfoChecker result = userChecker.getUserInfoCheck(accessUser);

        // then
        assertThat(result.getUserId()).isEqualTo("63b4ecab-856f-4277-8a1d-6ebe5afc4b77");
        assertThat(result.getEmail()).isEqualTo("test");
        assertThat(result.getUserRole()).isEqualTo("PENDING");

        mockServer.verify();    // 모든 기대가 충족됐는지 확인
    }

    /** 여러 JSON 변형을 파싱 테스트 */
    private static Stream<Arguments> provideJsonCases() {
        return Stream.of(
                Arguments.of(EXPECTED_JSON, null, null),
                Arguments.of(EXPECTED_JSON_WITH_NICKNAME, "tester", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideJsonCases")
    void getUserInfoCheck_parsingVariants(String jsonBody,
                                          String expectedNickname,
                                          Integer expectedSemester)  {
        // given
        LoginAccessUser accessUser = new LoginAccessUser("test", "test");
        String expectedUrl =
                "http://user-service.guideme.svc.cluster.local:9000/api/user/login"
                        + "?email=test&student_id=test";

        mockServer.reset();
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonBody, MediaType.APPLICATION_JSON));

        // when
        UserInfoChecker result = userChecker.getUserInfoCheck(accessUser);

        // then
        assertThat(result.getUserId()).isEqualTo("63b4ecab-856f-4277-8a1d-6ebe5afc4b77");
        assertThat(result.getEmail()).isEqualTo("test");
        assertThat(result.getStudentId()).isEqualTo("test");
        assertThat(result.getUserRole()).isIn(List.of("ABLE", "PENDING"));
        assertThat(result.getNickname()).isEqualTo(expectedNickname);
        assertThat(result.getSemester()).isEqualTo(expectedSemester);
        System.out.println(result.toString());
        mockServer.verify();
    }
}
