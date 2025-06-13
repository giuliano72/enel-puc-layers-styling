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
    @OrderBy("listOrder")
    private List<LayerRule> styleRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("listOrder")
    private List<LayerRule> sizeRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("listOrder")
    private List<LayerRule> labelRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("listOrder")
    private List<LayerRule> tooltipRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("listOrder")
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

            for (int idx = 0; idx < mapStyleRules.size(); idx++) {
                Map<String, Object> mapLayerRule = mapStyleRules.get(idx);
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);

                LayerRule layerRule = new LayerRule(mapLayerRule);
                if (layerRule.getListOrder() == null) {
                    layerRule.setListOrder(idx);
                }
                this.styleRules.add(layerRule);
            };
        }

        this.sizeRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapSizeRules = (ArrayList<Map<String, Object>>) map.get("sizeRules");
        if (mapSizeRules != null) {

            for (int idx = 0; idx < mapSizeRules.size(); idx++) {
                Map<String, Object> mapLayerRule = mapSizeRules.get(idx);
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);

                LayerRule layerRule = new LayerRule(mapLayerRule);
                if (layerRule.getListOrder() == null) {
                    layerRule.setListOrder(idx);
                }

                this.sizeRules.add(layerRule);
            }
        }

        this.labelRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapLabelRules = (ArrayList<Map<String, Object>>) map.get("labelRules");
        if (mapLabelRules != null) {

            for (int idx = 0; idx < mapLabelRules.size(); idx++) {
                Map<String, Object> mapLayerRule = mapLabelRules.get(idx);
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);

                LayerRule layerRule = new LayerRule(mapLayerRule);
                if (layerRule.getListOrder() == null) {
                    layerRule.setListOrder(idx);
                }

                this.labelRules.add(layerRule);
            }
        }

        this.tooltipRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapTootipRules = (ArrayList<Map<String, Object>>) map.get("tooltipRules");
        if (mapTootipRules != null) {

            for (int idx = 0; idx < mapTootipRules.size(); idx++) {
                Map<String, Object> mapLayerRule = mapTootipRules.get(idx);
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);

                LayerRule layerRule = new LayerRule(mapLayerRule);
                if (layerRule.getListOrder() == null) {
                    layerRule.setListOrder(idx);
                }

                this.tooltipRules.add(layerRule);
            }
        }

        this.menuHeaderDataRules = new ArrayList<LayerRule>();
        ArrayList<Map<String, Object>> mapMenuHeaderDataRules = (ArrayList<Map<String, Object>>) map.get("menuHeaderDataRules");
        if (mapMenuHeaderDataRules != null) {

            for (int idx = 0; idx < mapMenuHeaderDataRules.size(); idx++) {
                Map<String, Object> mapLayerRule = mapMenuHeaderDataRules.get(idx);
                mapLayerRule.put("layerGroup", this.layerGroup);
                mapLayerRule.put("environment", this.environment);
                mapLayerRule.put("linked", Boolean.TRUE);

                LayerRule layerRule = new LayerRule(mapLayerRule);
                if (layerRule.getListOrder() == null) {
                    layerRule.setListOrder(idx);
                }

                this.menuHeaderDataRules.add(layerRule);
            }
        }

        this.existingStyleOverrideMode = (String) map.get("existingStyleOverrideMode");

    }

    public StyleDefinition(String layerGroup, String country, String environment) {
        this.layerGroup = layerGroup;
        this.country = country;
        this.environment = environment;
    }

}
