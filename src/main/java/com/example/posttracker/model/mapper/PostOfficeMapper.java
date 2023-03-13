package com.example.posttracker.model.mapper;

import com.example.posttracker.model.dto.PostOfficeDto;
import com.example.posttracker.model.entity.PostOffice;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PostOfficeMapper {

    PostOfficeMapper INSTANCE = Mappers.getMapper(PostOfficeMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    PostOffice postOfficeDtoToPostOffice (PostOfficeDto postOfficeDto);

    PostOfficeDto  postOfficeToPostOfficeDto (PostOffice postOffice);




}
