package guideme.imageservice.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Component
@ConfigurationProperties(prefix = "firebase")
public class FirebaseUploader {

    private String bucketName;
    private String urlFormat;

    public String uploadAndGetUrl(String filename, byte[] imageBytes) throws IOException {

        // Upload to Firebase
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create("images/" + filename, imageBytes, "image/jpeg");

        // Generate public URL
        return String.format(
                urlFormat,
                bucketName,
                URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8)
        );
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    
    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }
    
}