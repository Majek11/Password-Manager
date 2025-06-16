package dev.majek.passwordmanagement.services;

import java.util.List;
import dev.majek.passwordmanagement.domain.model.VaultItem;

public interface VaultService {
    VaultItem createVaultItem(VaultItem item);
    List<VaultItem> findAllByEmail(String email);
    VaultItem findById(String id);
    void deleteById(String id);
    VaultItem updateVaultItem(VaultItem updatedItem);
}
