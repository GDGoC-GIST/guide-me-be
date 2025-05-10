package guideme.imageservice.domain;

import guideme.imageservice.util.Id.IdHolder;
import guideme.imageservice.util.clock.ClockHolder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Image {

    private static final String JPG_PREFIX = ".jpg";
    private final String id;
    private final  String uploaderId;

    private final String filename;
    private final String filepath;

    private final int width;
    private final int height;
    private final long sizeInBytes;

    private long uploadedAt;

    public static Image create(
            IdHolder idHolder, ClockHolder clockHolder, String uploaderId
    ) {
        return Image.builder()
                .id(idHolder.generate())
                .uploaderId(uploaderId)
                .filename(idHolder.generate() + JPG_PREFIX)
                .uploadedAt(clockHolder.current())
                .build();
    }

    public Image updateImageData(
            String filepath, int width, int height, long sizeInBytes
    ) {
        return toBuilder().filepath(filepath).width(width).height(height).sizeInBytes(sizeInBytes).build();
    }

    // Methods
}