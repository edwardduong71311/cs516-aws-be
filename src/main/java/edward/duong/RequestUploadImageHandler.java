package edward.duong;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import edward.duong.aws.s3.ImageStorage;
import edward.duong.model.UploadFile;
import edward.duong.payload.ApiResponse;
import edward.duong.payload.record.UploadImagePayload;
import edward.duong.payload.record.UploadImageResponse;
import edward.duong.util.Mapper;
import edward.duong.util.TokenHelper;

import java.util.Objects;

public class RequestUploadImageHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ImageStorage storage = new ImageStorage();
    private static final String CREDENTIALS_MANDATORY = "Credentials are mandatory";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String email = TokenHelper.getEmailFromToken(event.getHeaders());
            if (Objects.isNull(email)) {
                return ApiResponse.generateResponse(null);
            }

            UploadImagePayload payload = Mapper.OBJECT_MAPPER.readValue(event.getBody(), UploadImagePayload.class);
            if (Objects.isNull(payload) ||
                    Objects.isNull(payload.name()) || payload.name().isEmpty() ||
                    Objects.isNull(payload.type()) || payload.type().isEmpty()
            ) {
                return ApiResponse.generateResponse(new UploadImageResponse(CREDENTIALS_MANDATORY, null));
            }

            String url = storage.generatePutUrl(UploadFile.builder()
                    .name(email + "/" + payload.name())
                    .contentType(payload.type()).build());

            return ApiResponse.generateResponse(new UploadImageResponse("", url));
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return ApiResponse.generateResponse(null);
        }
    }
}
