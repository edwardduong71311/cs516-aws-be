package edward.duong.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.List;

public class EnvConfig {
    public static String REGION = "us-east-1";

    public static String BUCKET = "";
    public static Integer GET_EXP_IN_MINUTE = 0;
    public static Integer BUCKET_PUT_EXP = 0;
    public static String TABLE = "";
    public static Integer TOKEN_EXP = 0;
    public static String TOKEN_SECRET = "";

    static {
        SsmClient ssmClient = SsmClient.builder()
                .region(Region.of(REGION))
                .build();

        List<String> paramNames = List.of(
            "CS516_BUCKET",
            "CS516_BUCKET_GET_EXP_IN_MINUTE",
            "CS516_BUCKET_PUT_EXP",
            "CS516_REGION",
            "CS516_TABLE",
            "CS516_TOKEN_EXP",
            "CS516_TOKEN_SECRET"
        );

        GetParametersRequest getRequest = GetParametersRequest.builder()
                .names(paramNames)
                .withDecryption(true)
                .build();
        GetParametersResponse response = ssmClient.getParameters(getRequest);

        for (Parameter param : response.parameters()) {
            switch (param.name()) {
                case "CS516_BUCKET":
                    BUCKET = param.value();
                    break;
                case "CS516_BUCKET_GET_EXP_IN_MINUTE":
                    GET_EXP_IN_MINUTE = Integer.valueOf(param.value());
                    break;
                case "CS516_BUCKET_PUT_EXP":
                    BUCKET_PUT_EXP = Integer.valueOf(param.value());
                    break;
                case "CS516_REGION":
                    REGION = param.value();
                    break;
                case "CS516_TABLE":
                    TABLE = param.value();
                    break;
                case "CS516_TOKEN_EXP":
                    TOKEN_EXP = Integer.valueOf(param.value());
                    break;
                case "CS516_TOKEN_SECRET":
                    TOKEN_SECRET = param.value();
                    break;
            }
        }
    }
}
