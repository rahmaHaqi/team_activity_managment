package com.mat.api.service.impl;

import com.mat.api.core.errorhandling.businessexeption.BusinessException;
import com.mat.api.core.errorhandling.exeption.CommonStatusCode;
import com.mat.api.mapper.converter.IMapClassWithDto;
import com.mat.api.mapper.dto.CahierTestDto;
import com.mat.api.models.CahierTestEntity;
import com.mat.api.models.CahierTestRefEntity;
import com.mat.api.models.CasTestEntity;
import com.mat.api.models.ProjectEntity;
import com.mat.api.repository.CahierTestRefRepository;
import com.mat.api.repository.CahierTestRepository;
import com.mat.api.repository.CasTestRepository;
import com.mat.api.repository.ProjectRepository;
import com.mat.api.service.CahierTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CahierTestServiceImpl implements CahierTestService {
    private final CahierTestRepository cahierTestRepository;
    private final ProjectRepository projectRepository;
    private final CahierTestRefRepository cahierTestRefRepository;
    private final CasTestRepository casTestRepository;
    private final IMapClassWithDto mapper;

    @Override
    public CahierTestDto saveCahierTest(CahierTestDto cahierTestDto) {
        try {
            List<CahierTestRefEntity> referentiels = cahierTestRefRepository.findAllById(cahierTestDto.getReferentielIds());
            List<CasTestEntity> casTests = casTestRepository.findAllById(cahierTestDto.getCasTestIds());

            CahierTestEntity cahierTest = mapper.convert(cahierTestDto, CahierTestEntity.class);
            ProjectEntity project = projectRepository.findById(cahierTestDto.getProject_id())
                    .orElseThrow(() -> new BusinessException("Project not found with ID: " + cahierTestDto.getProject_id()));

            cahierTest.setReferentiels(referentiels);
            cahierTest.setProject(project);
            cahierTest.setCasTests(casTests);

            CahierTestEntity savedCahierTest = cahierTestRepository.save(cahierTest);
            return mapper.convert(savedCahierTest, CahierTestDto.class);
        } catch (DataAccessException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du CahierTest : " + e.getMessage());
        }
    }

    @Override
    public CahierTestDto getCahierTestById(Long id) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }
        CahierTestEntity cahierTest = cahierTestRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CahierTest not found with id: " + id));
        return mapper.convert(cahierTest, CahierTestDto.class);
    }

    @Override
    public CahierTestDto updateCahierTestDto(Long id, CahierTestDto cahierTestDto) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }
        CahierTestEntity cahierTest = cahierTestRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CahierTest not found with id: " + id));

        // Récupération des référentiels à partir des IDs
        List<CahierTestRefEntity> referentiels = cahierTestRefRepository.findAllById(cahierTestDto.getReferentielIds());

        // Récupération des cas de test à partir des IDs
        List<CasTestEntity> casTests = casTestRepository.findAllById(cahierTestDto.getCasTestIds());

        // Mise à jour des propriétés du CahierTestEntity
        cahierTest.setReferentiels(referentiels);
        cahierTest.setCasTests(casTests);
        cahierTest.setNom(cahierTestDto.getNom());
        cahierTest.setDescription(cahierTestDto.getDescription());

        // Enregistrement des modifications
        cahierTestRepository.save(cahierTest);

        // Conversion du CahierTestEntity en CahierTestDto et retour
        return mapper.convert(cahierTest, CahierTestDto.class);
    }


    @Override
    public void deleteCahierTest(Long id) {
        if (id == null) {
            throw new BusinessException(CommonStatusCode.ID_IS_MISSING);
        }
        cahierTestRepository.deleteById(id);
    }

    @Override
    public Page<CahierTestDto> getPagedListCahierTest(Integer page, Integer size) {
        try {
            page = (page == null || page < 1) ? 0 : page - 1;
            size = (size == null || size < 5) ? 5 : size;

            Pageable pageRequest = PageRequest.of(page, size);

            Page<CahierTestEntity> pageOfEntities = cahierTestRepository.findAll(pageRequest);

            if (!pageOfEntities.hasContent()) {
                throw new BusinessException(CommonStatusCode.NO_CONTENT_EXCEPTION);
            }

            return pageOfEntities.map(source -> {
                CahierTestDto result = mapper.convert(source, CahierTestDto.class);
                result.setProject_id(source.getProject() != null ? source.getProject().getId() : null);
                result.setProjectName(source.getProject() != null ? source.getProject().getName() : null);
                return result;
            });
        } catch (DataAccessException e) {
            throw new BusinessException("Erreur d'accès aux données lors de la récupération des CahierTests : " + e.getMessage());
        }
    }
}



