package guideme.imageservice.repository.user;

import guideme.imageservice.repository.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}