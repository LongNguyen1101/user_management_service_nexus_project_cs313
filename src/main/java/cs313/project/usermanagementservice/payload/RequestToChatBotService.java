package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class RequestToChatBotService {
    private String question;
    private List<QuestionAndAnswer> history;
}
