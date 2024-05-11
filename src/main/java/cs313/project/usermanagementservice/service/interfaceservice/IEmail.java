package cs313.project.usermanagementservice.service.interfaceservice;

public interface IEmail {
    Boolean sendMail(String recipient, String username, String otp);
}
