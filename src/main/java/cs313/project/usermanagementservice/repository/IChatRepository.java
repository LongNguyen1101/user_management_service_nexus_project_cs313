package cs313.project.usermanagementservice.repository;

import cs313.project.usermanagementservice.model.ChatHistory;
import cs313.project.usermanagementservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IChatRepository extends MongoRepository<ChatHistory, String> {
    List<ChatHistory> findByUser(User user);
}
