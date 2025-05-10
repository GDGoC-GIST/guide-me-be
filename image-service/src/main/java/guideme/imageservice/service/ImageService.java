package guideme.imageservice.service;

import guideme.imageservice.domain.Image;
import guideme.imageservice.repository.entity.ImageEntity;
import guideme.imageservice.dto.ProcessedImage;
import guideme.imageservice.repository.user.ImageRepository;
import guideme.imageservice.util.FirebaseUploader;
import guideme.imageservice.util.Id.IdHolder;
import guideme.imageservice.util.ImageProcessor;
import guideme.imageservice.util.clock.ClockHolder;
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

    private final IdHolder idHolder;
    private final ClockHolder clockHolder;

    @Transactional
    public String uploadImage(MultipartFile file, String userId) throws IOException {

        Image  image = Image.create(idHolder, clockHolder, userId);

        // 이미지 처리
        ProcessedImage processedImage = ImageProcessor.processImage(file);

        // Firebase 업로드
        String imageUrl = firebaseUploader.uploadAndGetUrl(image.getFilename(), processedImage.getImageBytes());

        // Image 메타 데이터 저장
        image = image.updateImageData(imageUrl, processedImage.getWidth(), processedImage.getHeight(),
                processedImage.getImageBytes().length);

        // DB 저장
        ImageEntity imageEntity = ImageEntity.toEntity(image);
        imageRepository.save(imageEntity);
        return imageUrl;
    }
}
