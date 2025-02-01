package edward.duong;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import edward.duong.aws.dynamodb.UserRepo;
import edward.duong.aws.s3.ImageStorage;
import edward.duong.model.User;
import edward.duong.payload.ApiResponse;
import edward.duong.payload.record.GetImageResponse;
import edward.duong.util.TokenHelper;

import java.util.Objects;

public class GetImageHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final UserRepo repo = new UserRepo();
    private static final ImageStorage storage = new ImageStorage();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String email = TokenHelper.getEmailFromToken(event.getHeaders());
            if (Objects.isNull(email)) {
                return ApiResponse.generateResponse(null);
            }

            User user = repo.getUserByEmail(email);
            if (Objects.isNull(user)) {
                return ApiResponse.generateResponse(null);
            }

            return ApiResponse.generateResponse(new GetImageResponse("", storage.generateGetUrl(user.getImage())));
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return ApiResponse.generateResponse(null);
        }
    }
}