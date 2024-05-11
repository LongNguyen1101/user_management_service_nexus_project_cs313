package cs313.project.usermanagementservice.repository;

import cs313.project.usermanagementservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String email);
}
