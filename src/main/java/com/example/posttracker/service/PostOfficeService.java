package com.example.posttracker.service;

import com.example.posttracker.constant.Status;
import com.example.posttracker.exception.AccessError;
import com.example.posttracker.exception.NotFoundException;
import com.example.posttracker.model.dto.PostOfficeDto;
import com.example.posttracker.model.dto.PostalItemFullDto;
import com.example.posttracker.model.entity.PostOffice;
import com.example.posttracker.model.entity.PostalItem;
import com.example.posttracker.model.entity.Postman;
import com.example.posttracker.model.mapper.PostOfficeMapper;
import com.example.posttracker.model.mapper.PostalItemMapper;
import com.example.posttracker.model.repository.PostOfficeRepository;
import com.example.posttracker.model.repository.PostalItemRepository;
import com.example.posttracker.model.repository.PostmanRepository;
import com.example.posttracker.model.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostOfficeService {
    private final PostOfficeRepository officeRepository;
    private final PostalItemRepository itemRepository;
    private final PostmanRepository postmanRepository;
    private final RecipientRepository recipientRepository;
    private final AccessTrack accessTrack;

    public List<PostOfficeDto> getAllOffice() {
        return officeRepository.findAll().stream()
                .map(PostOfficeMapper.INSTANCE::postOfficeToPostOfficeDto).collect(Collectors.toList());

    }

    public PostOfficeDto getOffice(Integer index) {
        return officeRepository.findById(index).map(PostOfficeMapper.INSTANCE::postOfficeToPostOfficeDto)
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске Почтового Офиса по индексу");
                    throw new NotFoundException("Почтовый Офис с таким индексом не найден");
                });
    }

    public List<PostalItemFullDto> getAllPostalInPostOffice(Integer index, Authentication authentication) {
        if (accessTrack.postOfficeAccess(index, authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО и пользователя вызывающего метод ");
            throw new AccessError("Нельзя посмотреть все отправления другого Почтового Офиса");
        }
        List<PostalItem> collect = officeRepository.findById(index).map(PostOffice::getPostalItems).orElseThrow(() -> {
            log.info("возникла ошибка при поиске Почтового Офиса по индексу");
            throw new NotFoundException("Почтовый Офис с таким индексом не найден");
        });
        return collect.stream().map(PostalItemMapper.INSTANCE::postalItemToPostalItemFullDto).collect(Collectors.toList());
    }

    public PostOfficeDto updateOffice(Integer index, PostOfficeDto postOfficeDto, Authentication authentication) {
        if (index != postOfficeDto.getIndex()) {
            throw new NotFoundException("Индекс изменяемого Почтового Офиса и индекс в переданном объекте не совпадают. " +
                    "Возможно вы допустили ошибку. Попробуйте снова.");
        }

        if (accessTrack.postOfficeAccess(index, authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО и пользователя вызывающего метод ");
            throw new AccessError("Нельзя изменять данные другого Почтового Офиса");
        }
        PostOffice postOffice = officeRepository.findById(index)
                .map((e) -> PostOfficeMapper.INSTANCE.postOfficeDtoToPostOffice(postOfficeDto))
                .orElseThrow(() -> {
                    log.info("возникла ошибка при поиске пользователя");
                    throw new NotFoundException("Пользователь с таким номером не найден.");
                });

        officeRepository.save(postOffice);
        return PostOfficeMapper.INSTANCE.postOfficeToPostOfficeDto(postOffice);
    }

    public PostOfficeDto addOffice(PostOfficeDto postOfficeDto) {
        if (officeRepository.findById(postOfficeDto.getIndex()).isPresent()) {
            throw new AccessError("Нельзя добавлять Почтовые Офисы с одинаковыми индексами. Убедитесь что не допустили ошибку");
        }

        PostOffice postOffice = PostOfficeMapper.INSTANCE.postOfficeDtoToPostOffice(postOfficeDto);
        officeRepository.save(postOffice);
        return PostOfficeMapper.INSTANCE.postOfficeToPostOfficeDto(postOffice);
    }


    public PostalItemFullDto addPostalToOffice(String tracker, String name) {
        log.info("вызван метод добавления посылки в другое ПО ");
        PostalItem item = itemRepository.findById(tracker).orElseThrow(() -> {
            log.info("возникла ошибка при поиске Почтового отправления ");
            throw new NotFoundException("Почтового отправления с таким трек номером не найдено");
        });
        PostOffice postOffice = postmanRepository.findById(name).map(Postman::getPostOffice).orElseThrow(() -> {
            log.info("возникла ошибка при поиске Почтового отправления у пользователя");
            throw new NotFoundException("Пользователь с таким идентификатором не найден");
        });
        Stack<Integer> route = item.getRoute();
        route.add(postOffice.getIndex());
        item.setRoute(route);
        item.setLastPostOffice(postOffice);
        item.setStatus(Status.WAITING);
        itemRepository.save(item);
        return PostalItemMapper.INSTANCE.postalItemToPostalItemFullDto(item);
    }

    public PostalItemFullDto deletePostalToOffice(String tracker, Authentication authentication) {
        PostalItem postalItem = checkPostalItem(tracker, authentication);
        postalItem.setStatus(Status.SHIPPED);
        itemRepository.save(postalItem);
        return PostalItemMapper.INSTANCE.postalItemToPostalItemFullDto(postalItem);
    }

    private PostalItem checkPostalItem(String tracker, Authentication authentication) {
        PostalItem postalItem = itemRepository.findById(tracker).orElseThrow(() -> {
            log.info("возникла ошибка при поиске Почтового отправления ");
            throw new NotFoundException("Почтового отправления с таким трек номером не найдено");
        });
        int index = postalItem.getLastPostOffice().getIndex();
        if (accessTrack.postOfficeAccess(index, authentication)) {
            log.info("возникла ошибка не соответствуют индексы ПО и пользователя вызывающего метод ");
            throw new AccessError("Нельзя изменять отправление из другого Почтового Офиса");
        }
        return postalItem;
    }

    public PostalItemFullDto givePostalToRecipient(String tracker, long phone, Authentication authentication) {
        PostalItem postalItem = checkPostalItem(tracker, authentication);
        long phoneItem = postalItem.getRecipient().getPhone();
        if (phoneItem!=phone){
            throw new AccessError(" введите 10 цифр номера телефона без первой цифры и специальных символов. " +
                    "Если номер был введен верно то у отправления с этим номером другой получатель.");
        }

        itemRepository.delete(postalItem);
        postalItem.setStatus(Status.RECEIVED);
        if (postalItem.getRecipient().getPostalItems().isEmpty()) {
            log.info("удаление записи о получателе отправления");
            recipientRepository.deleteById(phone);
        }
        return PostalItemMapper.INSTANCE.postalItemToPostalItemFullDto(postalItem);
    }

    public void deleteOffice(int index) {
    List<Postman> postmanList=  officeRepository.findById(index).map(PostOffice::getPostmans).orElseThrow(() -> {
          log.info("возникла ошибка при поиске Почтового отправления ");
          throw new NotFoundException("По с таким идентификатором не найден");
      });
    if (!postmanList.isEmpty()){
        log.info("возникла ошибка при удалении ПО ");
        throw new AccessError("Пока в ПО числятся сотрудники его нельзя удалить.");
    }
    officeRepository.deleteById(index);
    }
}
