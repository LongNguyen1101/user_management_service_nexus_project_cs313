package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseRegistration {
    private String otpId;
    private String token;
    private String errorMessage;
}
