package com.example.posttracker.model.mapper;


import com.example.posttracker.model.dto.CreatePostalDto;
import com.example.posttracker.model.dto.PostalItemDto;
import com.example.posttracker.model.dto.PostalItemFullDto;
import com.example.posttracker.model.entity.PostalItem;
import com.example.posttracker.model.entity.Recipient;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PostalItemMapper {
    PostalItemMapper INSTANCE = Mappers.getMapper(PostalItemMapper.class);

    @Mapping(target = "fullRoute", expression = "java(MapperToDto.routeToDto(postalItem.getRoute()))")
    @Mapping(source = "lastPostOffice.index", target = "currentPostOfficeIndex")
    PostalItemDto postalItemToPostalItemDto(PostalItem postalItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    PostalItem createPostalDtoToPostalItem(CreatePostalDto postalDto);

    @Mapping(source = "recipientPhone", target = "phone")
    @Mapping(source = "recipientName", target = "name")
    @Mapping(source = "recipientAddress", target = "address")
    Recipient createPostalDtoToRecipient(CreatePostalDto postalDto);

    @Mapping(target = "recipientPhone", source = "recipient.phone")
    @Mapping(target = "recipientName", source = "recipient.name")
    @Mapping(target = "recipientAddress", source = "recipient.address")
    @Mapping(target = "lastPostOfficeIndex", source = "lastPostOffice.index")
    @Mapping(target = "fullRoute", expression = "java(MapperToDto.routeToDto(postalItem.getRoute()))")
    PostalItemFullDto postalItemToPostalItemFullDto(PostalItem postalItem);
/*
    default String[] mappedRoute(Stack<String> route) {
        return route.stream().map(i -> URL + "{" + i + "}").toArray(String[]::new);
    }*/


}
