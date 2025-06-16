package dev.majek.passwordmanagement.domain.repository;
import java.util.List;

import dev.majek.passwordmanagement.domain.model.VaultItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VaultItemRepository extends MongoRepository<VaultItem, String> {
    List<VaultItem> findAllByEmail(String email);
}
