package com.appsdeveloperblog.app.ws.io.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

// As this class is persisted in a database as table, therefore entity annotation required
// name == tablename in database
@Entity(name="users")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    // =====
    // Below fields, in this UserEntity, must match the fields in UserDTO!
    // =====
    @Id // is a primary key
    @GeneratedValue // generated value annotation, auto incremented
    private long id;

    @Column(nullable = false) // makes this field required
    private String userId; // holds alphanumeric user id, save to pass around in network using http request

    @Column(nullable = false, length=50) // makes this field required and sets a max length
    private String firstName;

    @Column(nullable = false, length=50) // makes this field required and sets a max length
    private String lastName;

    @Column(nullable = false, length=120, unique = true) // makes this field required and sets a max length
    private String email;

    @Column(nullable = false) // makes this field required
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(nullable = false) // makes this field required
    private Boolean emailVerificationStatus = false; // explicitly set to false standard

    // important mapping!!
    @OneToMany(mappedBy ="userDetails", cascade=CascadeType.ALL) // when user details are persisted, all addresses will also propagate
    private List<AddressEntity> addresses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Boolean getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
    }

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }
}