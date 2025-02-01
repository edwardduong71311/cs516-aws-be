package edward.duong;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.Context;
import edward.duong.aws.dynamodb.UserRepo;
import edward.duong.model.User;
import edward.duong.payload.ApiResponse;
import edward.duong.payload.record.RegisterPayload;
import edward.duong.payload.record.RegisterResponse;
import edward.duong.util.Mapper;
import edward.duong.util.TokenHelper;

import java.util.Objects;

public class RegisterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final UserRepo repo = new UserRepo();
    private static final String CREDENTIALS_MANDATORY = "Credentials are mandatory";
    private static final String CANNOT_REGISTER = "Cannot register user";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            RegisterPayload payload = Mapper.OBJECT_MAPPER.readValue(event.getBody(), RegisterPayload.class);

            if (Objects.isNull(payload) ||
                    Objects.isNull(payload.email()) || payload.email().isEmpty() ||
                    Objects.isNull(payload.password()) || payload.password().isEmpty() ||
                    Objects.isNull(payload.name()) || payload.name().isEmpty()
            ) {
                return ApiResponse.generateResponse(new RegisterResponse(CREDENTIALS_MANDATORY, null));
            }

            User user = repo.register(User.builder()
                    .email(payload.email())
                    .password(payload.password())
                    .name(payload.name())
                    .build());
            if (Objects.isNull(user)) {
                return ApiResponse.generateResponse(new RegisterResponse(CANNOT_REGISTER, null));
            }

            user.setPassword(null);
            return ApiResponse.generateResponse(new RegisterResponse("", TokenHelper.generateToken(user.getEmail())));
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return ApiResponse.generateResponse(null);
        }
    }
}
