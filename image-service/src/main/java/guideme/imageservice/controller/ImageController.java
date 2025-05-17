package guideme.imageservice.controller;

import guideme.imageservice.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "timetable",
        description = "time table 업로드")
@RequestMapping("/api/user/timetable")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    @Operation(
            summary = "사진 업로드",
            description = "시간표 사진을 업로드하여 firebase로 올립니다"
    )
    public ResponseEntity<Map<String, String>> upload(
            @RequestHeader("X-Client-Id") String userId,
            @RequestParam("file") MultipartFile file)
    {
        try {
            String imagePath = imageService.uploadImage(file, userId);
            return ResponseEntity.ok(Map.of("imagePath", imagePath));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "업로드 실패: " + e.getMessage()));
        }
    }
}