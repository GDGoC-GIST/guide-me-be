package guideme.imageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessedImage {
    private final byte[] imageBytes;
    private final int width;
    private final int height;
}