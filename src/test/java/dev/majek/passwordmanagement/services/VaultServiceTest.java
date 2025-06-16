package dev.majek.passwordmanagement.services;

import java.util.List;
import java.util.Optional;
import dev.majek.passwordmanagement.domain.model.VaultItem;
import dev.majek.passwordmanagement.domain.repository.VaultItemRepository;
import dev.majek.passwordmanagement.exception.ResourceNotFoundException;
import dev.majek.passwordmanagement.exception.VaultItemValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VaultServiceTest {
    private VaultItemRepository vaultItemRepository;
    private VaultServiceImpl vaultService;
    private BCryptPasswordEncoder passwordEncoder;

    private VaultItem buildTestVaultItem() {
        return new VaultItem("1", "majekswag@gmail.com", "Email", "majekswag", "password123", "Private Note");
    }

    @BeforeEach
    void setUp() {
        this.vaultItemRepository = Mockito.mock(VaultItemRepository.class);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.vaultService = new VaultServiceImpl(this.vaultItemRepository, this.passwordEncoder);
    }

    @Test
    @DisplayName("User Can Create Vault")
    void testToShow_ThatUserCan_CreateVaultItem() {
        VaultItem item = this.buildTestVaultItem();
        Mockito.when(this.vaultItemRepository.save(Mockito.any(VaultItem.class))).thenReturn(item);
        VaultItem saved = this.vaultService.createVaultItem(item);
        Assertions.assertNotNull(saved);
        Assertions.assertEquals("Email", saved.getTitle());
        Assertions.assertEquals("majekswag@gmail.com", saved.getEmail());
        Mockito.verify(this.vaultItemRepository, Mockito.times(1)).save(Mockito.any(VaultItem.class));
    }

    @Test
    @DisplayName("Password is Hashed When User Creates Vault Item")
    void shouldHashPassword_WhenCreatingVaultItem() {
        VaultItem item = this.buildTestVaultItem();
        Mockito.when(this.vaultItemRepository.save(Mockito.any(VaultItem.class))).thenAnswer((invocation) -> invocation.getArgument(0));
        VaultItem saved = this.vaultService.createVaultItem(item);
        Assertions.assertNotEquals("password123", saved.getPassword());
        Assertions.assertTrue(this.passwordEncoder.matches("password123", saved.getPassword()));
        Mockito.verify(this.vaultItemRepository, Mockito.times(1)).save(Mockito.any(VaultItem.class));
    }

    @Test
    @DisplayName("Should return vault item when found by ID")
    void shouldReturnVaultItem_WhenFoundById() {
        VaultItem item = this.buildTestVaultItem();
        Mockito.when(this.vaultItemRepository.findById("1")).thenReturn(Optional.of(item));
        VaultItem found = this.vaultService.findById("1");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("1", found.getId());
        Mockito.verify(this.vaultItemRepository, Mockito.times(1)).findById("1");
    }

    @Test
    @DisplayName("Should throw exception when vault item not found by ID")
    void shouldThrowException_WhenVaultItemNotFoundById() {
        Mockito.when(this.vaultItemRepository.findById("1")).thenReturn(Optional.empty());
        Exception ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> this.vaultService.findById("1"));
        Assertions.assertEquals("Vault item not found for ID.", ex.getMessage());
    }

    @Test
    @DisplayName("Should return list of vault items by email")
    void shouldReturnListOfVaultItemsByEmail() {
        VaultItem item1 = this.buildTestVaultItem();
        VaultItem item2 = new VaultItem("2", "majekswag@gmail.com", "Bank", "majek", "pass456", "Note2");
        Mockito.when(this.vaultItemRepository.findAllByEmail("majekswag@gmail.com")).thenReturn(List.of(item1, item2));
        List<VaultItem> items = this.vaultService.findAllByEmail("majekswag@gmail.com");
        Assertions.assertEquals(2, items.size());
        Mockito.verify(this.vaultItemRepository, Mockito.times(1)).findAllByEmail("majekswag@gmail.com");
    }

    @Test
    @DisplayName("Should throw exception when email is blank in findAllByEmail")
    void shouldThrowException_WhenEmailBlankInFindAll() {
        Exception ex = Assertions.assertThrows(VaultItemValidationException.class, () -> this.vaultService.findAllByEmail(" "));
        Assertions.assertEquals("Email cannot be blank.", ex.getMessage());
    }

    @Test
    @DisplayName("Should delete vault item by ID")
    void shouldDeleteVaultItemById() {
        Mockito.when(this.vaultItemRepository.existsById("1")).thenReturn(true);
        this.vaultService.deleteById("1");
        Mockito.verify(this.vaultItemRepository, Mockito.times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should throw exception when vault item to delete does not exist")
    void shouldThrowException_WhenDeletingNonExistingVaultItem() {
        Mockito.when(this.vaultItemRepository.existsById("1")).thenReturn(false);
        Exception ex = Assertions.assertThrows(VaultItemValidationException.class, () -> this.vaultService.deleteById("1"));
        Assertions.assertEquals("Vault item not found for ID.", ex.getMessage());
    }

    @Test
    @DisplayName("Should update vault item with new values")
    void shouldUpdateVaultItemWithNewValues() {
        VaultItem existing = this.buildTestVaultItem();
        VaultItem updated = new VaultItem("1", "majekswag@gmail.com", "Updated Title", "newUser", "newPassword", "Updated Note");
        Mockito.when(this.vaultItemRepository.findById("1")).thenReturn(Optional.of(existing));
        Mockito.when(this.vaultItemRepository.save(Mockito.any(VaultItem.class))).thenAnswer((invocation) -> invocation.getArgument(0));
        VaultItem result = this.vaultService.updateVaultItem(updated);
        Assertions.assertEquals("Updated Title", result.getTitle());
        Assertions.assertNotEquals("newPassword", result.getPassword());
        Assertions.assertTrue(this.passwordEncoder.matches("newPassword", result.getPassword()));
        Mockito.verify(this.vaultItemRepository).save(Mockito.any(VaultItem.class));
    }

    @Test
    @DisplayName("Should throw if ID is missing in update")
    void shouldThrowException_WhenUpdateIdIsMissing() {
        VaultItem updated = new VaultItem((String)null, "email", "title", "user", "pass", "note");
        Exception ex = Assertions.assertThrows(VaultItemValidationException.class, () -> this.vaultService.updateVaultItem(updated));
        Assertions.assertEquals("Vault item ID cannot be blank.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw if vault item not found for update")
    void shouldThrowException_WhenUpdateItemNotFound() {
        VaultItem updated = this.buildTestVaultItem();
        Mockito.when(this.vaultItemRepository.findById("1")).thenReturn(Optional.empty());
        Exception ex = Assertions.assertThrows(VaultItemValidationException.class, () -> this.vaultService.updateVaultItem(updated));
        Assertions.assertEquals("Vault item not found for ID.", ex.getMessage());
    }
}
