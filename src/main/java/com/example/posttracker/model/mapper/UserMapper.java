package com.example.posttracker.model.mapper;

import com.example.posttracker.model.dto.PostmanDto;
import com.example.posttracker.model.dto.RegisterDto;
import com.example.posttracker.model.entity.Postman;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

@Mapping(target = "postOfficeIndex",source = "postOffice.index")
    PostmanDto PostmanToPostmanDto (Postman postman);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "postOffice",ignore = true)
    Postman PostmanDtoToPostman (PostmanDto postman);

    @Mapping(target ="postOffice", ignore = true)
    Postman RegisterToPostman (RegisterDto registerDto);
}
