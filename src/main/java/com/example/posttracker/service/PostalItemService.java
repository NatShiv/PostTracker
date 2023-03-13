package com.example.posttracker.service;

import com.example.posttracker.constant.Status;
import com.example.posttracker.exception.AccessError;
import com.example.posttracker.exception.NotFoundException;
import com.example.posttracker.model.dto.CreatePostalDto;
import com.example.posttracker.model.dto.PostalItemDto;
import com.example.posttracker.model.dto.PostalItemFullDto;
import com.example.posttracker.model.entity.PostOffice;
import com.example.posttracker.model.entity.PostalItem;
import com.example.posttracker.model.entity.Postman;
import com.example.posttracker.model.entity.Recipient;
import com.example.posttracker.model.mapper.PostalItemMapper;
import com.example.posttracker.model.repository.PostalItemRepository;
import com.example.posttracker.model.repository.PostmanRepository;
import com.example.posttracker.model.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Stack;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostalItemService {
    private final PostalItemRepository postalItemRepository;
    private final PostmanRepository postmanRepository;
    private final RecipientRepository recipientRepository;
    private final AccessTrack accessTrack;

    public PostalItemDto getUserItem(String tracker) {
        log.info("вызван метод поиска отправления по номеру для неавторизованных пользователей");
        PostalItem postalItem = postalItemRepository.findById(tracker).orElseThrow(() -> new NotFoundException("Посылка с таким трек номером не найдена"));
        return PostalItemMapper.INSTANCE.postalItemToPostalItemDto(postalItem);
    }

    public PostalItemFullDto getAdminItem(String tracker) {
        log.info("вызван метод поиска отправления по номеру для авторизованных пользователей");
        PostalItem postalItem = postalItemRepository.findById(tracker).orElseThrow(() -> new NotFoundException("Посылка с таким трек номером не найдена"));
        return PostalItemMapper.INSTANCE.postalItemToPostalItemFullDto(postalItem);
    }

    public PostalItemFullDto addPostalItem(CreatePostalDto postalDto, String name) {
        log.info("вызван метод добавления отправления");
        PostOffice postOffice = postmanRepository.findById(name).map(Postman::getPostOffice)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске по у пользователя");
                    throw new NotFoundException("Пользователь с таким номером не найден. Oбратитесь к администратору с описанием проблемы");
                });
        log.info("Сотрудник с полученным именем найден, почтовое отделение получено");
        String tracker;
        do {
            tracker = UUID.randomUUID().toString();
        } while (postalItemRepository.findById(tracker).isEmpty());

        Recipient recipient = PostalItemMapper.INSTANCE.createPostalDtoToRecipient(postalDto);
        PostalItem postalItem = PostalItemMapper.INSTANCE.createPostalDtoToPostalItem(postalDto);
        log.info("полученный обект преобразован в 2 сущности recipient и postalItem");

        postalItem.setTracker(tracker);
        postalItem.setRecipient(recipient);
        postalItem.setLastPostOffice(postOffice);
        postalItem.setStatus(Status.WAITING);

        Stack<Integer> route = new Stack<>();
        route.add(postOffice.getIndex());
        postalItem.setRoute(route);

        recipientRepository.save(recipient);
        log.info("сущность recipient сохранена");
        postalItemRepository.save(postalItem);
        log.info("сущность postalItem сохранена");
        return PostalItemMapper.INSTANCE.postalItemToPostalItemFullDto(postalItem);
    }


    public void deleteItem(String tracker, Authentication authentication) {
        log.info("вызван метод удаления отправления");
        if (accessTrack.postalAccess(tracker, authentication)) {
            log.info("возникла ошибка не соответствуют индексы по у посылки и пользователя");
            throw new AccessError("Нельзя удалить посылку находящуюся в другом отделении");
        }

        Recipient recipient = postalItemRepository.findById(tracker).map(PostalItem::getRecipient)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске получателя у отправления");
                    throw new NotFoundException("У посылки с таким трек номером не удалось получить получателя");
                });
        postalItemRepository.deleteById(tracker);
        if (recipient.getPostalItems().isEmpty()) {
            log.info("удаление записи о получателе отправления");
            recipientRepository.delete(recipient);
        }
    }
}
