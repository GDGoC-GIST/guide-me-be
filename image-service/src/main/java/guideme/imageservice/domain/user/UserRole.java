package guideme.imageservice.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 상태 코드", example = "ABLE")
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserRole {
    PENDING, ABLE, MASTER
}
