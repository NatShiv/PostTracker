package com.example.posttracker.service;

import com.example.posttracker.exception.AccessError;
import com.example.posttracker.exception.NotFoundException;
import com.example.posttracker.model.entity.PostOffice;
import com.example.posttracker.model.entity.PostalItem;
import com.example.posttracker.model.entity.Postman;
import com.example.posttracker.model.repository.PostalItemRepository;
import com.example.posttracker.model.repository.PostmanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccessTrack {
    private final PostmanRepository repository;
    private final PostalItemRepository postalItemRepository;

    boolean userAccess(String id, Authentication authentication) {
        int adminPostOfficeIndex = repository.findById(authentication.getName()).map(Postman::getPostOffice).map(PostOffice::getIndex)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске инедекса ПО у пользователя");
                    throw new NotFoundException("У вас не удалось получить индекс отделения в котором вы зарегистрированы");
                });
        int userPostOfficeIndex = repository.findById(id).map(Postman::getPostOffice).map(PostOffice::getIndex)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске инедекса по у пользователя");
                    throw new NotFoundException("У пользователя с таким номером не удалось получить индекс отделения в котором он зарегистрирован");
                });
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(e -> e.equals("ROLE_SUPER_ADMIN"))
                || userPostOfficeIndex != adminPostOfficeIndex;
    }

    boolean postOfficeAccess(int id, Authentication authentication) {
        int adminPostOfficeIndex = repository.findById(authentication.getName()).map(Postman::getPostOffice).map(PostOffice::getIndex)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске инедекса ПО у пользователя");
                    throw new NotFoundException("У вас не удалось получить индекс отделения в котором вы зарегистрированы");
                });

        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(e -> e.equals("ROLE_SUPER_ADMIN"))
                || id != adminPostOfficeIndex;
    }

    boolean postalAccess(String tracker, Authentication authentication) {
        int userPostOfficeIndex = repository.findById(authentication.getName()).map(Postman::getPostOffice).map(PostOffice::getIndex)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске инедекса по у пользователя");
                    throw new NotFoundException("У пользователя с таким номером не удалось получить индекс отделения в котором он зарегистрирован");
                });
        int postalPostOfficeIndex = postalItemRepository.findById(tracker)
                .map(PostalItem::getLastPostOffice).map(PostOffice::getIndex)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске инедекса у отправления");
                    throw new NotFoundException("У посылки с таким трек номером не удалось получить индекс отделения в котором она находится");
                });

        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(e -> e.equals("ROLE_SUPER_ADMIN"))
                || userPostOfficeIndex != postalPostOfficeIndex;
    }

    void checkDefault(String id) {
        if (id.equals("000000admin")) {
            log.info("возникла ошибка нельзя удалить предзаписанного пользователя ");
            throw new AccessError("Нельзя удалить или изменить предзаписанного пользователя ");
        }
    }
}
