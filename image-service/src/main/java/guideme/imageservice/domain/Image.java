package guideme.imageservice.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Image {

    private Long id;
    private String uploaderId;

    private String filename;
    private String filepath;

    private int width;
    private int height;
    private long sizeInBytes;

    private LocalDateTime uploadedAt;
    
    @Builder
    private Image(
        Long id, String uploaderId, String filename, String filepath, int width, int height, long sizeInBytes, LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.uploaderId = uploaderId;
        this.filename = UUID.randomUUID() + ".jpg";
        this.filepath = filepath;
        this.width = width;
        this.height = height;
        this.sizeInBytes = sizeInBytes;
        this.uploadedAt = LocalDateTime.now();
    }

    // Methods
}