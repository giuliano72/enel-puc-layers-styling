package com.enel.layersstyling.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "style_definition")
public class StyleDefinition {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String layerGroup;

    @Column
    private String country;

    @Column
    private String environment;

    @Column
    private String classIds;

    @Column
    private String tags;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MapLayerStyle> styles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayerRule> styleRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayerRule> sizeRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayerRule> labelRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayerRule> tooltipRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LayerRule> menuHeaderDataRules;

    @Column
    private String existingStyleOverrideMode;


    public StyleDefinition() {

    }

    public StyleDefinition(Map<String, Object> map) {
        this.id = (Long) map.get("id");
        this.layerGroup = (String) map.get("layerGroup");
        this.country = (String) map.get("country");
        this.environment = (String) map.get("environment");
        this.classIds = (String) map.get("classIds");
        this.tags = (String) map.get("tags");

        this.styles = new ArrayList<MapLayerStyle>();
        ArrayList<Map<String, Object>> mapStyles = (ArrayList<Map<String, Object>>) map.get("styles");
        if (mapStyles != null) {
            mapStyles.forEach(mapStyle -> {
                this.styles.add(new MapLayerStyle(mapStyle));
            });
        }


        this.styleRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapStyleRules = (ArrayList<Map<String, Object>>) map.get("styleRules");
        if (mapStyleRules != null) {
            mapStyleRules.forEach(mapLayerRule -> {
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);
                this.styleRules.add(new LayerRule(mapLayerRule));
            });
        }

        this.sizeRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapSizeRules = (ArrayList<Map<String, Object>>) map.get("sizeRules");
        if (mapSizeRules != null) {
            mapSizeRules.forEach(mapLayerRule -> {
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);
                this.sizeRules.add(new LayerRule(mapLayerRule));
            });
        }

        this.labelRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapLabelRules = (ArrayList<Map<String, Object>>) map.get("labelRules");
        if (mapLabelRules != null) {
            mapLabelRules.forEach(mapLayerRule -> {
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);
                this.labelRules.add(new LayerRule(mapLayerRule));
            });
        }

        this.tooltipRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapTootipRules = (ArrayList<Map<String, Object>>) map.get("tooltipRules");
        if (mapTootipRules != null) {
            mapTootipRules.forEach(mapLayerRule -> {
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);
                this.tooltipRules.add(new LayerRule(mapLayerRule));
            });
        }

        this.menuHeaderDataRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapMenuHeaderDataRules = (ArrayList<Map<String, Object>>) map.get("menuHeaderDataRules");
        if (mapMenuHeaderDataRules != null) {
            mapMenuHeaderDataRules.forEach(mapLayerRule -> {
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);
                this.menuHeaderDataRules.add(new LayerRule(mapLayerRule));
            });
        }

        this.existingStyleOverrideMode = (String) map.get("existingStyleOverrideMode");

    }

    public StyleDefinition(String layerGroup, String country, String environment) {
        this.layerGroup = layerGroup;
        this.country = country;
        this.environment = environment;
    }

}
