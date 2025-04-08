package guideme.authservice.infrastructure.dto.response;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GlobalResponse<T> {
    private final String timeStamp;
    private final int status;
    private final boolean isSuccess;
    private final T data;

    @Builder(access = AccessLevel.PRIVATE)
    public GlobalResponse(T data, boolean isSuccess, int status, String timeStamp) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public static <T> GlobalResponse<T> fail(T data, int status) {
        return GlobalResponse.<T>builder()
                .timeStamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .status(status)
                .isSuccess(false)
                .data(data)
                .build();
    }

    public static <T> GlobalResponse<T> success(T data, int status) {
        return GlobalResponse.<T>builder()
                .timeStamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .status(status)
                .isSuccess(true)
                .data(data)
                .build();
    }
}
