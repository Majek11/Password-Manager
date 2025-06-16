package dev.majek.passwordmanagement.domain.repository;

import dev.majek.passwordmanagement.domain.model.VaultItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VaultRepository extends MongoRepository<VaultItem, String> {
}