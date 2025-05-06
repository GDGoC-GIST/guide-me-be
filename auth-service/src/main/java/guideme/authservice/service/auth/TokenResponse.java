package guideme.authservice.service.auth;

public record TokenResponse( String token_type, long expires_in, String id_token) {
}
