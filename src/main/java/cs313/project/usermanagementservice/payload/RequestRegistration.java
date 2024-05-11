package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RequestRegistration {
    private String username;
    private String email;
    private String password;
    private List<String> favoriteGenres;
}
