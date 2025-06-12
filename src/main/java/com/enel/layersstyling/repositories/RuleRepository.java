package com.enel.layersstyling.repositories;
import com.enel.layersstyling.entities.LayerRule;
import com.enel.layersstyling.entities.StyleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepository extends JpaRepository<LayerRule, Long>  {

    List<LayerRule> findByLayerGroupAndEnvironmentAndLinked(String layerGroup, String environment, Boolean linked);
    List<LayerRule> findByLayerGroupAndEnvironmentAndLinkedAndValue(String layerGroup, String environment, Boolean linked, String value);

    List<LayerRule> deleteByLinked(Boolean linked);
    List<LayerRule> deleteByLayerGroupAndEnvironmentAndLinked(String layerGroup, String environment, Boolean linked);

    /*
    List<LayerRule> findByLayerGroupAndLinked(String layerGroup, Boolean linked);
    List<LayerRule> findByLayerGroupAndEnvironmentAndLinked(String layerGroup, String environment, Boolean linked);


    List<LayerRule> deleteByLayerGroupAndLinked(String layerGroup, Boolean linked);
    List<LayerRule> deleteByLayerGroupAndEnvironmentAndLinked(String layerGroup, String environment, Boolean linked);
    */
}
