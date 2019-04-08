package ca.gc.aafc.seqdb.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Path;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.seqdb.api.dto.GroupDto;
import ca.gc.aafc.seqdb.api.dto.PcrBatchDto;
import ca.gc.aafc.seqdb.api.dto.PcrPrimerDto;
import ca.gc.aafc.seqdb.api.dto.PcrReactionDto;
import ca.gc.aafc.seqdb.api.dto.ProductDto;
import ca.gc.aafc.seqdb.api.dto.RegionDto;
import ca.gc.aafc.seqdb.api.dto.vocabularies.BaseVocabularyDto;
import ca.gc.aafc.seqdb.api.repository.VocabularyReadOnlyRepository;
import ca.gc.aafc.seqdb.api.repository.JpaDtoRepository;
import ca.gc.aafc.seqdb.api.repository.JpaRelationshipRepository;
import ca.gc.aafc.seqdb.api.repository.JpaResourceRepository;
import ca.gc.aafc.seqdb.api.repository.filter.RsqlFilterHandler;
import ca.gc.aafc.seqdb.api.repository.filter.SimpleFilterHandler;
import ca.gc.aafc.seqdb.api.repository.handlers.JpaDtoMapper;
import ca.gc.aafc.seqdb.api.repository.meta.JpaTotalMetaInformationProvider;
import ca.gc.aafc.seqdb.api.security.authorization.ReadableGroupFilterHandlerFactory;
import ca.gc.aafc.seqdb.entities.Group;
import ca.gc.aafc.seqdb.entities.PcrBatch;
import ca.gc.aafc.seqdb.entities.PcrPrimer;
import ca.gc.aafc.seqdb.entities.PcrReaction;
import ca.gc.aafc.seqdb.entities.Product;
import ca.gc.aafc.seqdb.entities.Region;

@Configuration
@EntityScan("ca.gc.aafc.seqdb.entities")
public class ResourceRepositoryConfig {

  @Inject
  private SimpleFilterHandler simpleFilterHandler;
  
  @Inject
  private RsqlFilterHandler rsqlFilterHandler;
  
  @Inject
  private JpaTotalMetaInformationProvider metaInformationProvider;
  
  @Inject
  private ReadableGroupFilterHandlerFactory groupFilterFactory;
  
  /**
   * Configures DTO-to-Entity mappings.
   * 
   * @return the DtoJpaMapper
   */
  @Bean
  public JpaDtoMapper dtoJpaMapper() {
    Map<Class<?>, Class<?>> jpaEntities = new HashMap<>();

    jpaEntities.put(RegionDto.class, Region.class);
    jpaEntities.put(PcrPrimerDto.class, PcrPrimer.class);
    jpaEntities.put(PcrBatchDto.class, PcrBatch.class);
    jpaEntities.put(PcrReactionDto.class, PcrReaction.class);
    jpaEntities.put(GroupDto.class, Group.class);
    jpaEntities.put(ProductDto.class, Product.class);

    return new JpaDtoMapper(jpaEntities);
  }
  
  @Bean
  public JpaTotalMetaInformationProvider metaInformationProvider(EntityManager entityManager) {
    return new JpaTotalMetaInformationProvider(entityManager, dtoJpaMapper());
  }

  @Bean
  public JpaResourceRepository<PcrPrimerDto> pcrPrimerRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrPrimerDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaResourceRepository<RegionDto> regionRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaResourceRepository<PcrBatchDto> pcrBatchRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrBatchDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaResourceRepository<PcrReactionDto> pcrReactionRepository(
      JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        PcrReactionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("pcrBatch").get("group"))
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaResourceRepository<GroupDto> groupRepository(
      JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        GroupDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> (Path<Group>) root)
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaResourceRepository<ProductDto> productRepository(JpaDtoRepository dtoRepository) {
    return new JpaResourceRepository<>(
        ProductDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public VocabularyReadOnlyRepository vocabularyDto(){
    return new VocabularyReadOnlyRepository();
  }

  @Bean
  public JpaRelationshipRepository<PcrPrimerDto, RegionDto> primerToRegionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrPrimerDto.class,
        RegionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaRelationshipRepository<PcrBatchDto, PcrReactionDto> pcrBatchToPcrReactionRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrBatchDto.class,
        PcrReactionDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("pcrBatch").get("group"))
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<PcrBatchDto, GroupDto> pcrBatchToGroupRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrBatchDto.class,
        GroupDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> (Path<Group>) root)
        ),
        metaInformationProvider
    );
  }

  @Bean
  public JpaRelationshipRepository<PcrReactionDto, PcrBatchDto> pcrReactionToPcrBatchRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        PcrReactionDto.class,
        PcrBatchDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            rsqlFilterHandler,
            groupFilterFactory.create(root -> root.get("group"))
        ),
        metaInformationProvider
    );
  }
  
  @Bean
  public JpaRelationshipRepository<ProductDto, GroupDto> productToGroupRepository(
      JpaDtoMapper dtoJpaMapper, JpaDtoRepository dtoRepository) {
    return new JpaRelationshipRepository<>(
        ProductDto.class,
        GroupDto.class,
        dtoRepository,
        Arrays.asList(
            simpleFilterHandler,
            groupFilterFactory.create(root -> (Path<Group>) root)
        ),
        metaInformationProvider
    );
  }
  

}
