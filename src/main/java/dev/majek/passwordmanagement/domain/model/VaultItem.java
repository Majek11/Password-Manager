package dev.majek.passwordmanagement.domain.model;

import jakarta.validation.constraints.*;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("vault_items")
public class VaultItem {
    @Id
    private String id;
    private String email;
    private String title;
    private String userName;
    private String password;
    private String notes;

    @Generated
    public String getId() {
        return this.id;
    }

//    @Generated
//    @NotBlank(message = "Email is required")
//    @Email( message = "Email should be valid")
    public String getEmail() {
        return this.email;
    }

    @Generated
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    public String getTitle() {
        return this.title;
    }

    @Generated
    @NotBlank(message = "Username is required")
    public String getUserName() {
        return this.userName;
    }

    @Generated
    @NotBlank(message = "Password is required")
    public String getPassword() {
        return this.password;
    }

    @Generated
    public String getNotes() {
        return this.notes;
    }

    @Generated
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public void setTitle(final String title) {
        this.title = title;
    }

    @Generated
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setNotes(final String notes) {
        this.notes = notes;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof VaultItem other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$email = this.getEmail();
                Object other$email = other.getEmail();
                if (this$email == null) {
                    if (other$email != null) {
                        return false;
                    }
                } else if (!this$email.equals(other$email)) {
                    return false;
                }

                Object this$title = this.getTitle();
                Object other$title = other.getTitle();
                if (this$title == null) {
                    if (other$title != null) {
                        return false;
                    }
                } else if (!this$title.equals(other$title)) {
                    return false;
                }

                Object this$userName = this.getUserName();
                Object other$userName = other.getUserName();
                if (this$userName == null) {
                    if (other$userName != null) {
                        return false;
                    }
                } else if (!this$userName.equals(other$userName)) {
                    return false;
                }

                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null) {
                    if (other$password != null) {
                        return false;
                    }
                } else if (!this$password.equals(other$password)) {
                    return false;
                }

                Object this$notes = this.getNotes();
                Object other$notes = other.getNotes();
                if (this$notes == null) {
                    return other$notes == null;
                } else return this$notes.equals(other$notes);
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof VaultItem;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        Object $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $notes = this.getNotes();
        result = result * 59 + ($notes == null ? 43 : $notes.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = this.getId();
        return "VaultItem(id=" + var10000 + ", email=" + this.getEmail() + ", title=" + this.getTitle() + ", userName=" + this.getUserName() + ", password=" + this.getPassword() + ", notes=" + this.getNotes() + ")";
    }

    @Generated
    public VaultItem(final String id, final String email, final String title, final String userName, final String password, final String notes) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.userName = userName;
        this.password = password;
        this.notes = notes;
    }

    @Generated
    public VaultItem() {
    }
}
