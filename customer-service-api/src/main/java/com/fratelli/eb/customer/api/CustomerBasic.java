package com.fratelli.eb.customer.api;


import lombok.Getter;

@Getter
public class CustomerBasic {

    private final java.util.UUID UUID;
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    public CustomerBasic() {
        this.UUID = null;
        this.name = null;
        this.surname = null;
        this.email = null;
        this.password = null;
    }

    public CustomerBasic(java.util.UUID UUID, String name, String surname, String email, String password) {
        this.UUID = UUID;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }


}
