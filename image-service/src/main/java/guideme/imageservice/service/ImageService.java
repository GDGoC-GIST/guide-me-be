package guideme.imageservice.service;

import guideme.imageservice.domain.Image;
import guideme.imageservice.domain.ImageEntity;
import guideme.imageservice.dto.ProcessedImage;
import guideme.imageservice.repository.ImageRepository;
import guideme.imageservice.util.FirebaseUploader;
import guideme.imageservice.util.ImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final FirebaseUploader firebaseUploader;

    @Transactional
    public String uploadImage(MultipartFile file, String userId) throws IOException {
        
        Image image = Image.builder()
                        .uploaderId(userId)                
                        .build();

        // 이미지 처리
        ProcessedImage processedImage = ImageProcessor.processImage(file);

        // Firebase 업로드
        String imageUrl = firebaseUploader.uploadAndGetUrl(image.getFilename(), processedImage.getImageBytes());

        // Image 메타 데이터 저장
        image.setFilepath(imageUrl);
        image.setWidth(processedImage.getWidth());
        image.setHeight(processedImage.getHeight());
        image.setSizeInBytes(processedImage.getImageBytes().length);

        // DB 저장
        ImageEntity imageEntity = ImageEntity.toEntity(image);
        imageRepository.save(imageEntity);

        return imageUrl;
    }
}
