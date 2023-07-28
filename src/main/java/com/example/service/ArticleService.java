package com.example.service;

import com.example.dto.ArticleDTO;
import com.example.entity.ArticleEntity;
import com.example.enums.ArticleStatus;
import com.example.enums.Language;
import com.example.exp.AppBadRequestException;
import com.example.mapper.ArticleShortInfoIMapper;
import com.example.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ArticleService {
    private ArticleRepository articleRepository;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ArticleTypesService articleTypesService;
    @Autowired
    private AttachService attachService;


    public ArticleDTO create(ArticleDTO dto, Integer moderatorId) {
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setSharedCount(dto.getSharedCount());
        entity.setImageId(dto.getImageId());
        entity.setRegionId(dto.getRegionId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setStatus(ArticleStatus.NOT_PUBLISHED);

        articleRepository.save(entity);
        articleTypesService.create(entity.getId(), dto.getArticleType());
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public ArticleDTO update(String id, ArticleDTO dto, Integer moderatorId) {
        // check
        ArticleEntity entity = get(id);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setDescription(dto.getDescription());
        entity.setImageId(dto.getImageId());
        entity.setRegionId(dto.getRegionId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setStatus(ArticleStatus.NOT_PUBLISHED);
        articleRepository.save(entity); // save

        articleTypesService.merge(entity.getId(), dto.getArticleType()); // save type list
        // response
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public ArticleDTO changeStatus(String id, ArticleDTO dto, ArticleStatus status, Integer publisherId) {
        ArticleEntity entity = get(id);
        if (status == ArticleStatus.PUBLISHED){
            entity.setStatus(ArticleStatus.PUBLISHED);
        }
        if (status == ArticleStatus.NOT_PUBLISHED){
            entity.setStatus(ArticleStatus.NOT_PUBLISHED);
        }
        articleRepository.save(entity);
        articleTypesService.merge(entity.getId(), dto.getArticleType());
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public ArticleDTO getById(String articleId, Language language) {
        ArticleEntity entity = get(articleId);
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        // dto.setImageId(entity.getImageId());
        dto.setRegion(regionService.getById(entity.getRegionId(), language));
        //
        return dto;
    }

    public List<ArticleDTO> getLast(Integer articleTypeId, int limit) {
        List<ArticleShortInfoIMapper> list = articleRepository.getLast5ArticleByArticleTypeIdNative(articleTypeId, limit);
        List<ArticleDTO> dtoList = new LinkedList<>();

        list.forEach(mapper -> {
            ArticleDTO dto = new ArticleDTO();
            dto.setId(mapper.getId());
            dto.setTitle(mapper.getTitle());
            dto.setDescription(mapper.getDescription());
            //dto.setImageId(mapper.getImageId());
            dto.setImage(attachService.getAttachWithUrl(mapper.getImageId()));

            dtoList.add(dto);
        });

        return dtoList;
    }

    public ArticleEntity get(String id) {
        return articleRepository.findById(id).orElseThrow(() -> {
            throw new AppBadRequestException("Article not found");
        });
    }
}