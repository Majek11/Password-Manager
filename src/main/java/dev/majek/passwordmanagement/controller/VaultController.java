package dev.majek.passwordmanagement.controller;


import java.util.List;

import dev.majek.passwordmanagement.domain.model.User;
import dev.majek.passwordmanagement.domain.model.VaultItem;
import dev.majek.passwordmanagement.services.VaultServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultServiceImpl vaultService;

    @PostMapping
    public ResponseEntity<VaultItem> createVaultItem(@Valid  @RequestBody VaultItem item, @AuthenticationPrincipal User authenticatedUser) {
//        System.out.println("createVaultItem" + item);
        item.setEmail(authenticatedUser.getEmail());
        return ResponseEntity.ok(vaultService.createVaultItem(item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(
            @PathVariable String id,
            @AuthenticationPrincipal User authenticatedUser) {

        VaultItem item = vaultService.findById(id);

        if (item == null) {
            return ResponseEntity.status(404).body("Item not found");
        }

        if (!item.getEmail().equals(authenticatedUser.getEmail())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(item);
    }


    @GetMapping
    public ResponseEntity<List<VaultItem>> getItemsForCurrentUser(
            @AuthenticationPrincipal User authenticatedUser) {
        List<VaultItem> items = vaultService.findAllByEmail(authenticatedUser.getEmail());
        return ResponseEntity.ok(items);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable String id,
            @RequestBody VaultItem updatedData,
            @AuthenticationPrincipal User authenticatedUser) {

        VaultItem existing = vaultService.findById(id);

        if (!existing.getEmail().equals(authenticatedUser.getEmail())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        updatedData.setId(id);
        updatedData.setEmail(authenticatedUser.getEmail());

        VaultItem saved = vaultService.updateVaultItem(updatedData);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            @PathVariable String id,
            @AuthenticationPrincipal User authenticatedUser) {

        VaultItem existing = vaultService.findById(id);

        if (!existing.getEmail().equals(authenticatedUser.getEmail())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        vaultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
