package cs313.project.usermanagementservice.repository;

import cs313.project.usermanagementservice.model.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface IOtpVerification extends MongoRepository<OtpVerification, String> {

}
