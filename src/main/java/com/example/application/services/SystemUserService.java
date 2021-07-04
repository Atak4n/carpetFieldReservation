package com.example.application.services;

import com.example.application.models.SystemUser;

public interface SystemUserService {
    SystemUser login(String email, String password);
    SystemUser save(SystemUser systemUser);
}