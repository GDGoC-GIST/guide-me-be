package guideme.authservice.service.token;

import guideme.authservice.domain.token.model.Token;
import guideme.authservice.domain.token.value.TokenType;
import guideme.authservice.domain.user.model.UserDto;
import guideme.authservice.infrastructure.dto.TokenPairResponse;
import guideme.authservice.service.token.provider.TokenProvider;
import guideme.authservice.service.token.reader.TokenReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenReader tokenReader;
    private final TokenProvider tokenProvider;

    public TokenPairResponse generateTokenPair(UserDto userDto) {
        String accessTokenValue = tokenProvider.generateToken(TokenType.ACCESS, userDto.getUserId(), userDto.getUserRole());
        String refreshTokenValue = tokenProvider.generateToken(TokenType.REFRESH, userDto.getUserId(), userDto.getUserRole());
        return new TokenPairResponse(accessTokenValue, refreshTokenValue);
    }

    public TokenPairResponse generateNewAccessToken(String refreshTokenValue) {
        Token refreshToken = tokenReader.handle(refreshTokenValue);
        String accessTokenValue = tokenProvider.generateToken(TokenType.ACCESS, refreshToken.getUserId(),
                refreshToken.getRole());
        return new TokenPairResponse(accessTokenValue, refreshTokenValue);
    }
}
