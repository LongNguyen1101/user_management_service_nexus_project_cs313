package cs313.project.usermanagementservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatHistory {
    @Id
    private String chatId;
    @DBRef
    private User user;
    private String question;
    private String answer;
}
