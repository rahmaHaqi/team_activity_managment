package com.mat.api.service.impl;

import com.mat.api.core.errorhandling.businessexeption.BusinessException;
import com.mat.api.core.errorhandling.exeption.CommonStatusCode;
import com.mat.api.mapper.converter.IMapClassWithDto;
import com.mat.api.mapper.dto.ConfigurationReferenceDTO;
import com.mat.api.models.ConfigurationEntity;
import com.mat.api.models.ConfigurationReferenceEntity;
import com.mat.api.repository.ConfigurationReferenceRepository;
import com.mat.api.repository.ConfigurationRepository;
import com.mat.api.service.ConfigurationReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationReferenceServiceImpl implements ConfigurationReferenceService {

    private final ConfigurationReferenceRepository configurationReferenceRepository;
    private final ConfigurationRepository configurationRepository;
    private final IMapClassWithDto mapper;

    @Override
    public ConfigurationReferenceDTO saveConfigurationReference(ConfigurationReferenceDTO configurationReferenceDTO) {
        ConfigurationReferenceEntity configurationReferenceEntity = new ConfigurationReferenceEntity();
        List<ConfigurationEntity> configurations = configurationRepository.findAllById(configurationReferenceDTO.getConfigurationIds());
        configurationReferenceEntity.setName(configurationReferenceDTO.getName());
        configurationReferenceEntity.setContent(configurationReferenceDTO.getContent());
        configurationReferenceEntity.getConfigurations().addAll(configurations);

        configurationReferenceEntity = configurationReferenceRepository.save(configurationReferenceEntity);
        return mapper.convert(configurationReferenceEntity, ConfigurationReferenceDTO.class);
    }

    @Override
    public ConfigurationReferenceDTO getConfigurationReferenceById(Long id) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }
        ConfigurationReferenceEntity configurationReferenceEntity = configurationReferenceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ConfigurationReference Not Found with id: " + id));
        return mapper.convert(configurationReferenceEntity, ConfigurationReferenceDTO.class);
    }

    @Override
    public ConfigurationReferenceDTO updateConfigurationReference(Long id, ConfigurationReferenceDTO configurationReferenceDTO) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }

        // Check if existing configuration with the given ID exists
        ConfigurationReferenceEntity existingConfigurationReference = configurationReferenceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonStatusCode.ID_IS_MISSING));

        // Update modifiable fields of the existing configuration reference
        existingConfigurationReference.setName(configurationReferenceDTO.getName());
        existingConfigurationReference.setContent(configurationReferenceDTO.getContent());
        // Make sure other potentially modifiable fields are also updated

        // Save the changes
        ConfigurationReferenceEntity updatedConfigurationReference = configurationReferenceRepository.save(existingConfigurationReference);
        return mapper.convert(updatedConfigurationReference, ConfigurationReferenceDTO.class);
    }

    @Override
    public void deleteConfigurationReferenceDTO(Long id) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }
        configurationReferenceRepository.deleteById(id);
    }

    @Override
    public Page<ConfigurationReferenceDTO> getPagedListConfigurationReferenceDTO(Integer page, Integer size) {
        page = (page == null || page < 1) ? 0 : page - 1;
        size = (size == null || size < 5) ? 5 : size;
        Pageable pageRequest = PageRequest.of(page, size);
        Page<ConfigurationReferenceEntity> pageOfEntities = configurationReferenceRepository.findAll(pageRequest);
        if (CollectionUtils.isEmpty(pageOfEntities.getContent())) {
            throw new BusinessException(CommonStatusCode.NO_CONTENT_EXCEPTION);
        }
        return pageOfEntities.map(source -> mapper.convert(source, ConfigurationReferenceDTO.class));
    }
}

