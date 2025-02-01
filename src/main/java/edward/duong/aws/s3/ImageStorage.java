package edward.duong.aws.s3;

import edward.duong.config.EnvConfig;
import edward.duong.model.UploadFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

public class ImageStorage {
    private static final String BUCKET_NAME = EnvConfig.BUCKET;
    private static final S3Presigner SIGNER = S3Presigner.create();
    private static final Duration EXPIRATION = Duration.ofSeconds(EnvConfig.BUCKET_PUT_EXP);
    private static final Duration GET_EXPIRATION = Duration.ofSeconds(EnvConfig.GET_EXP_IN_MINUTE * 60);

    public String generatePutUrl(UploadFile file) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(file.getName())
                    .contentType(file.getContentType())
                    .build();

            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                    .signatureDuration(EXPIRATION)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest response = SIGNER.presignPutObject(request);
            return response.url().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String generateGetUrl(String filePath) {
        try {
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                    .signatureDuration(GET_EXPIRATION)
                    .getObjectRequest(req -> req.bucket(BUCKET_NAME).key(filePath))
                    .build();

            PresignedGetObjectRequest response = SIGNER.presignGetObject(request);
            return response.url().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
