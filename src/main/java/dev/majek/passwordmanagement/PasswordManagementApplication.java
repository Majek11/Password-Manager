package dev.majek.passwordmanagement;

import dev.majek.passwordmanagement.domain.repository.UserRepository;
import lombok.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class PasswordManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner test(UserRepository repo) {
        return args -> {
            System.out.println("User count: " + repo.count());
        };
    }


    @Bean
    CommandLineRunner passwordmanager(MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("Connected to MongoDB database: " +
                    mongoTemplate.getDb().getName());
            System.out.println("Collections: " +
                    mongoTemplate.getCollectionNames());
        };
    }

}
