package guideme.imageservice.util;

import guideme.imageservice.dto.ProcessedImage;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageProcessor {

    private static final int IMAGE_WIDTH = 1080;

    public static ProcessedImage processImage(MultipartFile file) throws IOException {
 
        // 1. 원본 이미지 읽기
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 2. 리사이즈 (1080px 초과 시)
        BufferedImage resizedImage = originalImage;
        if (originalWidth > IMAGE_WIDTH) {
            int newHeight = (int) ((IMAGE_WIDTH / originalWidth) * originalHeight);
            resizedImage = Thumbnails.of(originalImage)
                                     .size(IMAGE_WIDTH, newHeight)
                                     .asBufferedImage();
        }

        // 3. JPEG 포맷으로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        return new ProcessedImage(imageBytes, resizedImage.getWidth(), resizedImage.getHeight());
    }
}