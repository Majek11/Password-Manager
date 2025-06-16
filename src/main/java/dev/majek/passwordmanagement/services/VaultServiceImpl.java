package dev.majek.passwordmanagement.services;


import java.util.List;
import dev.majek.passwordmanagement.domain.model.VaultItem;
import dev.majek.passwordmanagement.domain.repository.VaultItemRepository;
import dev.majek.passwordmanagement.exception.ResourceNotFoundException;
import dev.majek.passwordmanagement.exception.VaultItemValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class VaultServiceImpl implements VaultService {
    private final VaultItemRepository vaultItemRepository;
    private final PasswordEncoder passwordEncoder;

    public VaultServiceImpl(VaultItemRepository vaultItemRepository, PasswordEncoder passwordEncoder) {
        this.vaultItemRepository = vaultItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public VaultItem createVaultItem(VaultItem item) {
        if (item.getPassword() != null && !item.getPassword().isBlank()) {
            if (item.getEmail() != null && !item.getEmail().isBlank()) {
                if (item.getTitle() != null && !item.getTitle().isBlank()) {
                    item.setPassword(this.passwordEncoder.encode(item.getPassword()));
                    return this.vaultItemRepository.save(item);
                } else {
                    throw new VaultItemValidationException("Title cannot be blank.");
                }
            } else {
                throw new VaultItemValidationException("Email cannot be blank.");
            }
        } else {
            throw new VaultItemValidationException("Password cannot be blank.");
        }
    }

    public List<VaultItem> findAllByEmail(String email) {
        if (email != null && !email.isBlank()) {
            return this.vaultItemRepository.findAllByEmail(email);
        } else {
            throw new VaultItemValidationException("Email cannot be blank.");
        }
    }

    public VaultItem findById(String id) {
        return (VaultItem)this.vaultItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vault item not found for ID."));
    }

    public void deleteById(String id) {
        if (!this.vaultItemRepository.existsById(id)) {
            throw new VaultItemValidationException("Vault item not found for ID.");
        } else {
            this.vaultItemRepository.deleteById(id);
        }
    }

    public VaultItem updateVaultItem(VaultItem updatedItem) {
        if (updatedItem.getId() != null && !updatedItem.getId().isBlank()) {
            VaultItem existing = (VaultItem)this.vaultItemRepository.findById(updatedItem.getId()).orElseThrow(() -> new VaultItemValidationException("Vault item not found for ID."));
            if (updatedItem.getPassword() != null && !updatedItem.getPassword().isBlank()) {
                updatedItem.setPassword(this.passwordEncoder.encode(updatedItem.getPassword()));
            } else {
                updatedItem.setPassword(existing.getPassword());
            }

            return (VaultItem)this.vaultItemRepository.save(updatedItem);
        } else {
            throw new VaultItemValidationException("Vault item ID cannot be blank.");
        }
    }
}
