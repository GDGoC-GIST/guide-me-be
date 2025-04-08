package guideme.authservice.service.token.reader;

import guideme.authservice.domain.token.model.Token;

public interface TokenReader {
    Token handle(String tokenValue);
}
