package edward.duong.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFile {
    String name;
    String contentType;
}
