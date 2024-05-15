package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class RequestChat {
    private String userId;
    private String question;
}
