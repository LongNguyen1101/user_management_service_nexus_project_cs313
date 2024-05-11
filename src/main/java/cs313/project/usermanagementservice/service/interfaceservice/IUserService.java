package cs313.project.usermanagementservice.service.interfaceservice;

import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.payload.RequestLogin;
import cs313.project.usermanagementservice.payload.RequestRegistration;
import cs313.project.usermanagementservice.payload.ResponseLogin;
import cs313.project.usermanagementservice.payload.ResponseRegistration;

public interface IUserService {
    ResponseRegistration saveUser(RequestRegistration requestRegistration);
    ResponseLogin login(RequestLogin requestLogin);
}
