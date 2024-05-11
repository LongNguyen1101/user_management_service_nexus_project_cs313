package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestLogin {
    private String email;
    private String password;
}
