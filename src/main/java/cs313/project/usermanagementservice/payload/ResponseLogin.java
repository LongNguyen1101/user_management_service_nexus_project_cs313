package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ResponseLogin {
    private String token;
    private String userId;
    private String username;
    private String userEmail;
    private List<String> favoriteGenres;
    private String role;
    private Integer httpCode;
    private String errMessage;
    private String otpId;
}
