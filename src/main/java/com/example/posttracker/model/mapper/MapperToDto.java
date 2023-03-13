package com.example.posttracker.model.mapper;

import com.example.posttracker.model.dto.PostOfficeDto;
import com.example.posttracker.model.repository.PostOfficeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class MapperToDto {
    private static PostOfficeRepository repositoryPO;

  static   List<PostOfficeDto> routeToDto(Stack<Integer> route) {
        List<PostOfficeDto> routeDto = new ArrayList<>(route.size());
        for (Integer e : route) {
            routeDto.add(PostOfficeMapper.INSTANCE
                    .postOfficeToPostOfficeDto(repositoryPO.findById(e)
                            .orElseGet(() -> repositoryPO.findById(000000).get())));
        }
    return routeDto;}
}
