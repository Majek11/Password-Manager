package dev.majek.passwordmanagement.domain.repository;


import java.util.Optional;
import dev.majek.passwordmanagement.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
