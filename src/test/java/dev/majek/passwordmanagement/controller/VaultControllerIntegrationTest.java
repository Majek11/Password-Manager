package dev.majek.passwordmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.majek.passwordmanagement.domain.model.User;
import dev.majek.passwordmanagement.domain.model.VaultItem;
import dev.majek.passwordmanagement.domain.repository.UserRepository;
import dev.majek.passwordmanagement.domain.repository.VaultItemRepository;
import dev.majek.passwordmanagement.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VaultControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private VaultItemRepository vaultItemRepository;

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        vaultItemRepository.deleteAll();

        testUser = new User();
        testUser.setFullName("Majek Swag");
        testUser.setEmail("majekswag@example.com");
        testUser.setMasterPasswordHash(passwordEncoder.encode("password123"));
        testUser.setVerified(true);
        testUser = userRepository.save(testUser);

        token = jwtTokenProvider.generateToken(testUser);
    }

    @Test
    @DisplayName("Should create vault item for authenticated user")
    void shouldCreateVaultItemForAuthenticatedUser() throws Exception {
        VaultItem request = new VaultItem();
        request.setTitle("Twitter");
        request.setUserName("majekswag");
        request.setPassword("pass123");
        request.setNotes("personal");

        mockMvc.perform(post("/api/vault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("majekswag@example.com"))
                .andExpect(jsonPath("$.title").value("Twitter"));
    }

    @Test
    @DisplayName("Should ignore client-supplied email and use authenticated user’s email")
    void shouldIgnoreEmailFieldAndUseAuthenticatedUserEmail() throws Exception {
        VaultItem request = new VaultItem();
        request.setTitle("Instagram");
        request.setUserName("intruder");
        request.setPassword("hack");
        request.setNotes("Hacked email");
        request.setEmail("hacker@evil.com"); // This should be ignored

        mockMvc.perform(post("/api/vault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("majekswag@example.com"));
    }

    @Test
    @DisplayName("Should reject unauthenticated vault creation")
    void shouldRejectVaultCreationWithoutToken() throws Exception {
        VaultItem request = new VaultItem();
        request.setTitle("LinkedIn");
        request.setUserName("majekswag");
        request.setPassword("pass123");
        request.setNotes("Work");

        mockMvc.perform(post("/api/vault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should still work even if email field is missing in request")
    void shouldStillWorkIfEmailIsMissingInRequest() throws Exception {
        VaultItem request = new VaultItem();
        request.setTitle("GitHub");
        request.setUserName("majekswag");
        request.setPassword("secure");
        request.setNotes("Dev stuff");

        mockMvc.perform(post("/api/vault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("majekswag@example.com"));
    }

    @Test
    @DisplayName("Should return only vault items belonging to authenticated user")
    void shouldReturnVaultItemsForAuthenticatedUserOnly() throws Exception {
        // Save one item for the logged-in user
        VaultItem item1 = new VaultItem();
        item1.setTitle("Google");
        item1.setUserName("user1");
        item1.setPassword("123");
        item1.setNotes("Personal");
        item1.setEmail(testUser.getEmail());
        vaultItemRepository.save(item1);

        // Save another item for someone else
        VaultItem item2 = new VaultItem();
        item2.setTitle("Facebook");
        item2.setUserName("attacker");
        item2.setPassword("hack");
        item2.setNotes("Not yours");
        item2.setEmail("intruder@example.com");
        vaultItemRepository.save(item2);


        mockMvc.perform(get("/api/vault")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Google"));
    }

    @Test
    @DisplayName("Should allow owner to fetch their vault item by ID")
    void shouldAllowOwnerToFetchItemById() throws Exception {
        VaultItem item = new VaultItem();
        item.setTitle("Twitter");
        item.setUserName("majekswag");
        item.setPassword("pass123");
        item.setNotes("Mine");
        item.setEmail(testUser.getEmail());
        item = vaultItemRepository.save(item);

        mockMvc.perform(get("/api/vault/" + item.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Twitter"));
    }

    @Test
    @DisplayName("Should forbid access to item not owned by user")
    void shouldForbidAccessToForeignVaultItem() throws Exception {
        VaultItem foreignItem = new VaultItem();
        foreignItem.setTitle("Private");
        foreignItem.setUserName("hacker");
        foreignItem.setPassword("pass123");
        foreignItem.setNotes("Not yours");
        foreignItem.setEmail("someoneelse@example.com");
        foreignItem = vaultItemRepository.save(foreignItem);

        mockMvc.perform(get("/api/vault/" + foreignItem.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 if vault item does not exist")
    void shouldReturnNotFoundForInvalidVaultId() throws Exception {
        mockMvc.perform(get("/api/vault/invalid-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should allow user to update their own vault item")
    void shouldAllowUpdateByOwner() throws Exception {
        VaultItem item = vaultItemRepository.save(new VaultItem(
                null, testUser.getEmail(), "Gmail", "me", "pass", "note"
        ));

        item.setTitle("Updated Gmail");
        item.setPassword("newpass");

        mockMvc.perform(put("/api/vault/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Gmail"));
    }

    @Test
    @DisplayName("Should forbid update to another user's vault item")
    void shouldForbidUpdateOfForeignItem() throws Exception {
        VaultItem otherItem = vaultItemRepository.save(new VaultItem(
                null, "attacker@example.com", "Bank", "attacker", "hack", "steal"
        ));

        otherItem.setTitle("Hacked");

        mockMvc.perform(put("/api/vault/" + otherItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(otherItem)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow user to delete their vault item")
    void shouldAllowOwnerToDeleteItem() throws Exception {
        VaultItem item = vaultItemRepository.save(new VaultItem(
                null, testUser.getEmail(), "Reddit", "user", "1234", "delete"
        ));

        mockMvc.perform(delete("/api/vault/" + item.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should forbid user from deleting someone else’s item")
    void shouldForbidDeletionByNonOwner() throws Exception {
        VaultItem item = vaultItemRepository.save(new VaultItem(
                null, "someone@example.com", "Instagram", "notyou", "pass", "steal"
        ));

        mockMvc.perform(delete("/api/vault/" + item.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
