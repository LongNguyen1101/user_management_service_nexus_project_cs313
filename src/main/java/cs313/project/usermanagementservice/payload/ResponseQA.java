package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResponseQA {
    private List<QuestionAndAnswer> questionAndAnswerList;
    private Integer httpCode;
    private String errMessage;
}
