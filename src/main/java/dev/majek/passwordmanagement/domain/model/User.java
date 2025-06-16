package dev.majek.passwordmanagement.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("users")
public class User {
    @Id
    private String id;
    @NotBlank
    private String fullName;
    @Email
    @NotBlank
    private String email;
    @Size(min = 6)
    @NotBlank
    private String masterPasswordHash;
    private boolean isVerified;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getFullName() {
        return this.fullName;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getMasterPasswordHash() {
        return this.masterPasswordHash;
    }

    @Generated
    public boolean isVerified() {
        return this.isVerified;
    }

    @Generated
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public void setMasterPasswordHash(final String masterPasswordHash) {
        this.masterPasswordHash = masterPasswordHash;
    }

    @Generated
    public void setVerified(final boolean isVerified) {
        this.isVerified = isVerified;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof User other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isVerified() != other.isVerified()) {
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

                Object this$fullName = this.getFullName();
                Object other$fullName = other.getFullName();
                if (this$fullName == null) {
                    if (other$fullName != null) {
                        return false;
                    }
                } else if (!this$fullName.equals(other$fullName)) {
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

                Object this$masterPasswordHash = this.getMasterPasswordHash();
                Object other$masterPasswordHash = other.getMasterPasswordHash();
                if (this$masterPasswordHash == null) {
                    return other$masterPasswordHash == null;
                } else return this$masterPasswordHash.equals(other$masterPasswordHash);
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isVerified() ? 79 : 97);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $fullName = this.getFullName();
        result = result * 59 + ($fullName == null ? 43 : $fullName.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $masterPasswordHash = this.getMasterPasswordHash();
        result = result * 59 + ($masterPasswordHash == null ? 43 : $masterPasswordHash.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = this.getId();
        return "User(id=" + var10000 + ", fullName=" + this.getFullName() + ", email=" + this.getEmail() + ", masterPasswordHash=" + this.getMasterPasswordHash() + ", isVerified=" + this.isVerified() + ")";
    }
}
