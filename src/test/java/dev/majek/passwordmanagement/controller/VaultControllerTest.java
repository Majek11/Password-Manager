package dev.majek.passwordmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.majek.passwordmanagement.domain.model.VaultItem;
import dev.majek.passwordmanagement.exception.DuplicateVaultItemException;
import dev.majek.passwordmanagement.exception.VaultItemNotFoundException;
import dev.majek.passwordmanagement.services.VaultServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VaultController.class)
class VaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaultServiceImpl vaultService;

    @Autowired
    private ObjectMapper objectMapper;

    private VaultItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new VaultItem("1", "majekswag@gmail.com", "Email", "user1", "password123", "note");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/vault - should create item")
    void shouldCreateVaultItem() throws Exception {
        Mockito.when(vaultService.createVaultItem(Mockito.any())).thenReturn(sampleItem);

        mockMvc.perform(post("/api/vault")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("majekswag@gmail.com")))
                .andExpect(jsonPath("$.title", is("Email")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/vault/{id} - should return item")
    void shouldGetVaultItemById() throws Exception {
        Mockito.when(vaultService.findById("1")).thenReturn(sampleItem);

        mockMvc.perform(get("/api/vault/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.email", is("majekswag@gmail.com")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/vault?email= - should return list")
    void shouldGetAllItemsByEmail() throws Exception {
        Mockito.when(vaultService.findAllByEmail("majekswag@gmail.com"))
                .thenReturn(Collections.singletonList(sampleItem));

        mockMvc.perform(get("/api/vault")
                        .param("email", "majekswag@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("majekswag@gmail.com")));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/vault/{id} - should update item")
    void shouldUpdateVaultItem() throws Exception {
        VaultItem updated = new VaultItem("1", "majekswag@gmail.com", "Updated", "user1", "newpass", "newnote");
        Mockito.when(vaultService.updateVaultItem(Mockito.any())).thenReturn(updated);

        mockMvc.perform(put("/api/vault/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/vault/{id} - should delete item")
    void shouldDeleteVaultItem() throws Exception {
        Mockito.doNothing().when(vaultService).deleteById("1");

        mockMvc.perform(delete("/api/vault/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/vault - should return 400 for missing required fields")
    void shouldReturnBadRequestForMissingFields() throws Exception {
        VaultItem invalidItem = new VaultItem();

        mockMvc.perform(post("/api/vault")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.message.title").exists())
                .andExpect(jsonPath("$.message.userName").exists())
                .andExpect(jsonPath("$.message.password").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/vault/{id} - should return 404 for non-existent ID")
    void shouldReturnNotFoundForInvalidIdOnUpdate() throws Exception {
        VaultItem updated = new VaultItem("999", "majekswag@gmail.com", "Updated", "user1", "newpass", "newnote");

        Mockito.when(vaultService.updateVaultItem(Mockito.any()))
                .thenThrow(new VaultItemNotFoundException("Item not found"));

        mockMvc.perform(put("/api/vault/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item Not Found")))
                .andExpect(jsonPath("$.message", is("Item not found")));
    }

    @Test
    @DisplayName("GET /api/vault/email - should return 401 for unauthenticated user")
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/vault/email")
                        .param("email", "majekswag@gmail.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/vault - should return 409 for duplicate entry")
    void shouldReturnConflictForDuplicateVaultItem() throws Exception {
        Mockito.when(vaultService.createVaultItem(Mockito.any()))
                .thenThrow(new DuplicateVaultItemException("Item already exists"));

        mockMvc.perform(post("/api/vault")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItem)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Duplicate Item")))
                .andExpect(jsonPath("$.message", is("Item already exists")));
    }
}
