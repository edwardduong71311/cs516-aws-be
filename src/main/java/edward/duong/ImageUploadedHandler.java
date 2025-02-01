package edward.duong;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import edward.duong.aws.dynamodb.UserRepo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ImageUploadedHandler implements RequestHandler<S3Event, String> {
    private static final UserRepo repo = new UserRepo();

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        s3Event.getRecords().forEach(record -> {
            String objectKey = record.getS3().getObject().getKey();

            String[] arr = objectKey.split("/");
            String owner = URLDecoder.decode(arr[0], StandardCharsets.UTF_8);

            repo.updateUserImage(owner, owner + "/" + arr[1]);
        });
        return "Processed S3 event";
    }
}
