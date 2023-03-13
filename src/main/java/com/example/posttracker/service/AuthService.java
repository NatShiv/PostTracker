package com.example.posttracker.service;

import com.example.posttracker.constant.RoleEnum;
import com.example.posttracker.exception.AccessError;
import com.example.posttracker.exception.NotFoundException;
import com.example.posttracker.model.dto.RegisterDto;
import com.example.posttracker.model.entity.Postman;
import com.example.posttracker.model.mapper.UserMapper;
import com.example.posttracker.model.repository.PostOfficeRepository;
import com.example.posttracker.model.repository.PostmanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsManager manager;
    private final AccessTrack accessTrack;
    private final PasswordEncoder encoder =new BCryptPasswordEncoder();
    private final PostmanRepository postmanRepository;
    private final PostOfficeRepository officeRepository;


    /**
     * Получить статус об авторизации пользователя
     *
     * @param userName логин пользователя (email)
     * @param password пароль пользователя
     * @return false, если пользователя не существует, либо true
     */

    public boolean login(String userName, String password) {
        log.info("вызван метод проверки авторизации");
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        return encoder.matches(password, encryptedPasswordWithoutEncryptionType);
    }

    /**
     * Получить статус о регистрации нового пользователя
     *
     * @param registerDto сущность с данными пользователя с формы регистрации
     * @param roleEnum       роль позльзователя USER / ADMIN
     * @return false, если пользователь существует в БД, true, если не существует в БД
     */

    public boolean register(RegisterDto registerDto, RoleEnum roleEnum, Authentication authentication) {
        log.info("вызван метод регистрации нового сотрудника");
        if (manager.userExists(registerDto.getId())) {
            log.info("вызван метод регистрации нового сотрудника");
            return false;
        }
      if (accessTrack.postOfficeAccess(registerDto.getPostOfficeIndex(), authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО у админа и пользователя");
            throw new AccessError("Нельзя зарегистрировать сотрудника в другом отделении");
        }
        manager.createUser(
                User.withDefaultPasswordEncoder()
                        .password(registerDto.getPassword())
                        .username(registerDto.getId())
                        .roles(roleEnum.name())
                        .build()
        );
        Postman postman = UserMapper.INSTANCE.RegisterToPostman(registerDto);
       postman.setPostOffice(officeRepository.findById(registerDto.getPostOfficeIndex()).orElseThrow(() -> {
            log.info("возникла ошибка при поиске ПО ");
            throw new NotFoundException("По указанному индексу не удалось найти ПО, в которое вы хотите зарегистрировать сотрудника");
        }));

        postmanRepository.save(postman);
        return true;
    }
}
