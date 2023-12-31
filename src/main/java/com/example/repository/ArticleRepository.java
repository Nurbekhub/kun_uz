package com.example.repository;

import com.example.entity.ArticleEntity;
import com.example.enums.ArticleStatus;
import com.example.mapper.ArticleShortInfoIMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends CrudRepository<ArticleEntity, String>,
        PagingAndSortingRepository<ArticleEntity, String> {


    @Query(" from ArticleEntity as s where s.categoryId =:categoryId order by s.createdDate")
    Page<ArticleEntity> findByCategory(@Param("categoryId") Integer categoryId,
                                       Pageable pageable);

    @Transactional
    @Modifying
    @Query("update ArticleEntity as r set r.title =:title,r.description =:description, r.content =:content, r.sharedCount =:sharedCount, r.imageId =:imageId, r.regionId =:regionId, r.categoryId =:categoryId where r.id =:id")
    String updateById(@Param("id") String id,
                   @Param("title") String title,
                   @Param("description") String description,
                   @Param("content") String content,
                   @Param("sharedCount") Integer shared_count,
                   @Param("imageId") String imageId,
                   @Param("regionId") Integer regionId,
                   @Param("categoryId") Integer categoryId);

    /*@Query("from ArticleEntity as a " +
            " inner join a.articleTypeSet as at" +
            " where at.articleTypeId =:articleTypeId  and a.status =:status and a.visible = true " +
            " order by a.publishedDate desc limit :limit")
    List<ArticleEntity> getLast5ArticleByArticleTypeId(@Param("articleTypeId") Integer articleTypeId,
                                                       @Param("status") ArticleStatus status,
                                                       @Param("limit") int limit);*/

    @Query(value = "select a.id, a.title, a.description, a.image_id, a.published_date from article as a " +
            " inner join article_types as at on at.article_id = a.id" +
            " where at.article_type_id =:articleTypeId  and a.status ='PUBLISHED' and a.visible = true " +
            " order by a.published_date desc limit :limit", nativeQuery = true)
    List<ArticleShortInfoIMapper> getLast5ArticleByArticleTypeIdNative(@Param("articleTypeId") Integer articleTypeId,
                                                                       @Param("limit") int limit);

    @Query(value = "select a.id, a.title, a.description, a.image_id, a.published_date from article as a " +
            " where a.id NOT IN (:articleList)  and a.status ='PUBLISHED' and a.visible = true " +
            " order by a.published_date desc limit :limit", nativeQuery = true)
    List<ArticleShortInfoIMapper> getLast8ArticleNotExistInListNative(@Param("articleList") List<String> articleList,
                                                                       @Param("limit") int limit);
    //9. Get Last 4 Article By Types and except given article id.
    @Query("from ArticleEntity as a " +
            " inner join a.articleTypeSet as at" +
            " where at.articleTypeId =:articleTypeId and a.id <>:articleId and a.status =:status and a.visible = true " +
            " order by a.publishedDate desc limit 4 ")
    List<ArticleShortInfoIMapper> getLast4ArticleByArticleTypeIdAndExcept(@Param("articleId") String articleId,
                                                                @Param("articleTypeId") Integer articleTypeId,
                                                                @Param("status") ArticleStatus status);
    @Query("from ArticleEntity as a " +
            " where a.visible = true " +
            " order by a.viewCount desc limit 4 ")
    List<ArticleShortInfoIMapper> getLast4MostReadArticles();

    @Query("from ArticleEntity as a " +
            " inner join a.articleTypeSet as at" +
            " where at.articleTypeId =:articleTypeId and a.regionId =:regionId and a.visible = true " +
            " order by a.publishedDate desc limit 5 ")
    List<ArticleShortInfoIMapper> getLast5ByArticleTypeIdAndRegionId(@Param("articleTypeId") Integer articleTypeId,
                                                                     @Param("regionId") Integer regionId);
}
