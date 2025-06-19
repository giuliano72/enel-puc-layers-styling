package com.enel.layersstyling.repositories;

import com.enel.layersstyling.entities.StyleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleDefinitonRepository extends JpaRepository<StyleDefinition, Long> {

    List<StyleDefinition> findByLayerGroupAndCountryAndEnvironment(String layerGroup, String country,String environment);
    List<StyleDefinition> findByLayerGroupAndCountryAndEnvironmentAndClassIds(String layerGroup, String country, String environment, String classIds);

    List<StyleDefinition> deleteByLayerGroupAndClassIds(String layerGroup, String classIds);
    List<StyleDefinition> deleteByLayerGroupAndCountryAndEnvironment(String layerGroup, String country,String environment);
    List<StyleDefinition> deleteByLayerGroup(String layerGroup);

    /*
    List<StyleDefinition> findByLayerGroup(String layerGroup);
    List<StyleDefinition> findByLayerGroupAndCountry(String layerGroup, String country);
    List<StyleDefinition> findByLayerGroupAndEnvironment(String layerGroup, String environment);

    List<StyleDefinition> deleteByLayerGroup(String layerGroup);
    List<StyleDefinition> deleteByLayerGroupAndCountry(String layerGroup, String country);
    List<StyleDefinition> deleteByLayerGroupAndEnvironment(String layerGroup, String environment);
    */
}
