package guideme.imageservice.repository.entity;

import guideme.imageservice.domain.Image;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uploaderId;

    private String filename;
    private String filepath;

    private int width;
    private int height;
    private long sizeInBytes;

    private LocalDateTime uploadedAt;

    public static ImageEntity toEntity(Image image) {
        return ImageEntity.builder()
            .id(image.getId())
            .uploaderId(image.getUploaderId())
            .filename(image.getFilename())
            .filepath(image.getFilepath())
            .width(image.getWidth())
            .height(image.getHeight())
            .sizeInBytes(image.getSizeInBytes())
            .uploadedAt(image.getUploadedAt())
            .build();
    }

    public Image toDomain() {
        return Image.builder()
            .id(id)
            .uploaderId(uploaderId)
            .filename(filename)
            .filepath(filepath)
            .width(width)
            .height(height)
            .sizeInBytes(sizeInBytes)
            .uploadedAt(uploadedAt)
            .build();
    }
}