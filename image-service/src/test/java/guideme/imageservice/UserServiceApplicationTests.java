package guideme.imageservice;

import guideme.imageservice.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceApplicationTests {

    @Autowired
    private ImageService imageService;

    @Test
    public void testImageUpload() throws IOException {
        // Load a test image from resources (e.g., src/test/resources/test-image.jpg)
        FileInputStream fis = new FileInputStream(ResourceUtils.getFile("classpath:test-image.jpg"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                fis
        );

        // Assume a dummy userId
        String userId = "test-user-123";

        // Upload image
        String imageUrl = imageService.uploadImage(file, userId);

        // Assertions
        assertThat(imageUrl).isNotNull();
        assertThat(imageUrl).contains("https://firebasestorage.googleapis.com");
    }
}