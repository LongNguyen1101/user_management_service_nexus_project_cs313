package cs313.project.usermanagementservice.service.interfaceservice;

import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.payload.*;

public interface IUserService {
    ResponseRegistration saveUser(RequestRegistration requestRegistration);
    ResponseLogin login(RequestLogin requestLogin);
    ResponseChatBot chatBot(RequestChat requestChat);
}
