package edward.duong.aws.dynamodb;

import edward.duong.config.EnvConfig;
import edward.duong.model.User;
import edward.duong.util.EncryptHelper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepo {
    private static final String TABLE_NAME = EnvConfig.TABLE;
    private final DynamoDbClient dynamoDbClient = DynamoDBClientProvider.getClient();

    private User getUser(String email) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":email", AttributeValue.builder().s(email).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("email = :email")
                .expressionAttributeValues(expressionValues)
                .limit(1)
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResponse.items();
        if (items.isEmpty()) {
            return null;
        }

        return User.builder()
                .email(items.get(0).get("email").s())
                .name(items.get(0).get("name").s())
                .password(items.get(0).get("password").s())
                .image(items.get(0).get("image").s())
                .build();
    }

    public User getUserByEmail(String email) {
        User user = getUser(email);
        if (Objects.isNull(user)) {
            return null;
        }
        return user;
    }

    public User login(String email, String password) {
        User user = getUser(email);
        if (Objects.isNull(user)) {
            return null;
        }
        if (!EncryptHelper.verify(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    public User register(User user) {
        User existed = getUser(user.getEmail());
        if (Objects.nonNull(existed)) {
            return null;
        }

        user.setPassword(EncryptHelper.encrypt(user.getPassword()));
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                    "email", AttributeValue.builder().s(user.getEmail()).build(),
                    "name", AttributeValue.builder().s(user.getName()).build(),
                    "password", AttributeValue.builder().s(user.getPassword()).build(),
                    "image", AttributeValue.builder().s("").build()
                ))
                .build();

        dynamoDbClient.putItem(request);
        return user;
    }

    public void updateUserImage(String email, String image) {
        System.out.println("Processing: User " + email + " with image " + image);
        User user = getUser(email);
        if (Objects.isNull(user)) {
            return;
        }

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(user.getEmail()).build());
        key.put("name", AttributeValue.builder().s(user.getName()).build());

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":image", AttributeValue.builder().s(image).build());

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression("SET image = :image")
                .expressionAttributeValues(expressionValues)
                .build();

        dynamoDbClient.updateItem(updateRequest);
        System.out.println("✅ Image updated on user profile");
    }
}
