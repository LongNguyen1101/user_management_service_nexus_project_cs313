package cs313.project.usermanagementservice.service.interfaceservice;

import cs313.project.usermanagementservice.model.ChatHistory;
import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.payload.*;

import java.util.List;

public interface IUserService {
    ResponseRegistration saveUser(RequestRegistration requestRegistration);
    ResponseLogin login(RequestLogin requestLogin);
    ResponseChatBot chatBot(RequestChat requestChat);
    ResponseQA getHistory(String userId);
}
