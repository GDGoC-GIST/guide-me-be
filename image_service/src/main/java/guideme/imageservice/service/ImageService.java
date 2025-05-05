package guideme.imageservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import guideme.imageservice.domain.Image;
import guideme.imageservice.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final String bucketName = "guide-me-timetable-image.firebasestorage.app";

    @Transactional
    public String uploadImage(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + ".jpg";

        // 1. 원본 이미지 읽기
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 2. 리사이즈 (1080px 초과 시)
        BufferedImage resizedImage = originalImage;
        if (originalWidth > 1080) {
            int newHeight = (int) ((1080.0 / originalWidth) * originalHeight);
            resizedImage = Thumbnails.of(originalImage)
                                     .size(1080, newHeight)
                                     .asBufferedImage();
        }

        // 3. JPEG 포맷으로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        // 4. Firebase 업로드
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create("images/" + filename, imageBytes, "image/jpeg");

        // 5. 공개 URL 생성
        String imageUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8));

        // 6. DB 저장
        Image image = new Image();
        image.setFilename(filename);
        image.setFilepath(imageUrl);
        imageRepository.save(image);

        return imageUrl;
    }
}
