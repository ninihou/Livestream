package com.example.livestream.member;

import java.io.Serializable;

public class Member implements Serializable {
    private String account;
    private String password;
    private String name;
    private String email;

    public Member() {
        super();
    }

    public Member(String account, String password, String name, String email) {
        super();
        this.account = account;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
