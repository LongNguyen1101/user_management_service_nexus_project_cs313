package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ResponseChatBot {
    private String response;
    private Integer httpCode;
    private String errMessage;
}
