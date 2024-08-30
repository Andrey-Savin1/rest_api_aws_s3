package ru.savin.rest_api_aws_s3.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.savin.rest_api_aws_s3.dto.EventDto;
import ru.savin.rest_api_aws_s3.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto map(Event event);

    @InheritInverseConfiguration
    Event map(EventDto eventDto);
}
