package com.example.posttracker.service;

import com.example.posttracker.exception.AccessError;
import com.example.posttracker.exception.NotFoundException;
import com.example.posttracker.model.dto.PostmanDto;
import com.example.posttracker.model.entity.Postman;
import com.example.posttracker.model.mapper.UserMapper;
import com.example.posttracker.model.repository.PostOfficeRepository;
import com.example.posttracker.model.repository.PostmanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final PostmanRepository repository;
    private final PostOfficeRepository officeRepository;
    private final AccessTrack accessTrack;

    public PostmanDto getPostman(String id) {
        Postman postman = repository.findById(id).orElseThrow(() -> {
            log.info("возникла ошибка при поиске пользователя");
            throw new NotFoundException("Пользователь с таким номером не найден.");
        });
        return UserMapper.INSTANCE.PostmanToPostmanDto(postman);
    }

    public void deletePostman(String id, Authentication authentication) {
        log.info("вызван метод удаления сотрудника");
        accessTrack.checkDefault(id);

        if (accessTrack.userAccess(id, authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО у админа и пользователя");
            throw new AccessError("Нельзя удалить сотрудника зарегистрированного в другом отделении");
        }
        repository.deleteById(id);
    }

    public PostmanDto patchPostman(String id, PostmanDto postmanDto, Authentication authentication) {
        log.info("вызван метод изменения сотрудника");

        if (!Objects.equals(postmanDto.getId(), id)) {
            throw new NotFoundException("Индекс изменяемого сотрудника и индекс в переданном объекте не совпадают. " +
                    "Возможно вы допустили ошибку. Попробуйте снова.");
        }

        if (accessTrack.userAccess(id, authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО у админа и пользователя");
            throw new AccessError("Нельзя изменить данные сотрудника зарегистрированного в другом отделении");
        }
        Postman postman = repository.findById(id).map((e) -> UserMapper.INSTANCE.PostmanDtoToPostman(postmanDto)).orElseThrow(() -> {
            log.info("возникла ошибка при поиске пользователя");
            throw new NotFoundException("Пользователь с таким номером не найден.");
        });

        postman.setPostOffice(officeRepository.findById(postmanDto.getPostOfficeIndex()).orElseThrow(() -> {
            log.info("возникла ошибка при поиске ПО ");
            throw new NotFoundException("По указанному индексу не удалось найти ПО, в которое вы хотите перевести сотрудника");
        }));
        repository.save(postman);
        return UserMapper.INSTANCE.PostmanToPostmanDto(postman);
    }
}
