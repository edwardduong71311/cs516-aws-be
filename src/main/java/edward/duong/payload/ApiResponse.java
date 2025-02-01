package edward.duong.payload;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import edward.duong.util.Mapper;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    public static APIGatewayProxyResponseEvent generateResponse(Object body) {
        APIGatewayProxyResponseEvent event = new APIGatewayProxyResponseEvent();
        try {
            String jsonBody = Mapper.OBJECT_MAPPER.writeValueAsString(body);
            event.setStatusCode(200);
            event.setBody(jsonBody);
        } catch (Exception e) {
            event.setStatusCode(500);
            event.setBody("{\"error\": \"Failed to serialize response\"}");
        }

        // Add CORS headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET, PUT,DELETE");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        event.setHeaders(headers);
        return event;
    }
}
