package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class RequestToChatBotService {
    private String question;
    private List<QuestionAndAnswer> history;
}
