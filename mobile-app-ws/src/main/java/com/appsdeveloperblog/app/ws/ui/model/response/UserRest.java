package com.appsdeveloperblog.app.ws.ui.model.response;

import java.util.List;

// Converts java object into outgoing JSON
// Contains user information that's send back, to confirm a user is stored in the database
public class UserRest {

    // note: different than database autoincrement field, i.e. this is not the database id, due to security reasons
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<AddressesRest> addresses;

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

    public List<AddressesRest> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressesRest> addresses) {
        this.addresses = addresses;
    }
}