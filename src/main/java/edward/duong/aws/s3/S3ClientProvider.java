package edward.duong.aws.s3;

import edward.duong.config.EnvConfig;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ClientProvider {
    private static final S3Client s3Client = S3Client.builder()
            .region(Region.of(EnvConfig.REGION))
            .build();

    public static S3Client getClient() {
        return s3Client;
    }
}
