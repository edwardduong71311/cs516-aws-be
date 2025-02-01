package edward.duong;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import edward.duong.aws.dynamodb.UserRepo;
import edward.duong.model.User;
import edward.duong.payload.record.LoginPayload;
import edward.duong.payload.record.LoginResponse;
import edward.duong.payload.ApiResponse;
import edward.duong.util.Mapper;
import edward.duong.util.TokenHelper;

import java.util.Objects;

public class LoginHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final UserRepo repo = new UserRepo();
    private static final String CREDENTIALS_MANDATORY = "Credentials are mandatory";
    private static final String CREDENTIALS_WRONG = "Wrong credentials";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            LoginPayload payload = Mapper.OBJECT_MAPPER.readValue(event.getBody(), LoginPayload.class);

            if (Objects.isNull(payload) ||
                    Objects.isNull(payload.email()) || payload.email().isEmpty() ||
                    Objects.isNull(payload.password()) || payload.password().isEmpty()
            ) {
                return ApiResponse.generateResponse(new LoginResponse(CREDENTIALS_MANDATORY, null));
            }

            User user = repo.login(payload.email(), payload.password());
            if (Objects.isNull(user)) {
                return ApiResponse.generateResponse(new LoginResponse(CREDENTIALS_WRONG, null));
            }

            return ApiResponse.generateResponse(new LoginResponse("", TokenHelper.generateToken(user.getEmail())));
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return ApiResponse.generateResponse(null);
        }
    }
}