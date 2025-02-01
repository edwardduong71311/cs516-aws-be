package edward.duong.aws.dynamodb;

import edward.duong.config.EnvConfig;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBClientProvider {
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.of(EnvConfig.REGION))
            .build();

    public static DynamoDbClient getClient() {
        return dynamoDbClient;
    }
}
