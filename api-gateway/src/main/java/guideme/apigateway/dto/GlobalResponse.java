package guideme.apigateway.dto;

public record GlobalResponse<T>(boolean success, T data, int status) {}
