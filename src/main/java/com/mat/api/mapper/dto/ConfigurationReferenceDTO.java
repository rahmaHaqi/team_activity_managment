package com.mat.api.mapper.dto;

import com.mat.api.core.crudbasic.mapper.dto.CustomDtoWithID;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigurationReferenceDTO extends CustomDtoWithID {

    private String name;
    private String content;
    private List<Long> configurationIds;
    private List<ConfigurationDto> configurations;


}
