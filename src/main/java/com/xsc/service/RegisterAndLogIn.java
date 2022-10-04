package com.xsc.service;

import com.xsc.domain.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface RegisterAndLogIn {
    byte[] saveImage(MultipartFile file);

    boolean register(User user);

    Integer logIn(User user);
}
