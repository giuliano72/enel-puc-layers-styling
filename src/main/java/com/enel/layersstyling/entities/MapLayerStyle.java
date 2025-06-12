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
@Table(name = "map_layer_style")
public class MapLayerStyle {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String styleId;

    @Column
    private String styleType; // 'POINT' | 'LINE' | 'POLYGON'

    @Column
    private String description;

    @Column
    private Boolean defaultStyle;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MapLayerStyleAttribute> attributes;

    @Column
    private String color;

    @Column
    private String iconUrl;

    @Column
    private String iconData;

    @Column
    private Integer zIndexOffset;

    @Column
    private Double weight;

    @Column
    private String dashArray;

    @Column
    private String fillColor;

    @Column
    private Double fillOpacity;

    @Column
    private Boolean fill;

    @Column
    private Boolean stroke;

    public MapLayerStyle() {
    }

    public MapLayerStyle(Map<String, Object> mapStyle) {
        this.id = (Long) mapStyle.get("id");
        this.styleId = (String) mapStyle.get("styleId");
        this.styleType = (String) mapStyle.get("styleType");
        this.description = (String) mapStyle.get("description");
        this.defaultStyle = (Boolean) mapStyle.get("defaultStyle");

        this.attributes = new ArrayList<MapLayerStyleAttribute>();
        ArrayList<Map<String, Object>> mapAttributes = (ArrayList<Map<String, Object>>) mapStyle.get("attributes");
        if (mapAttributes != null) {
            mapAttributes.forEach(mapAttribute -> {
                this.attributes.add(new MapLayerStyleAttribute(mapAttribute));
            });
        }

        this.color = (String) mapStyle.get("color");
        this.iconUrl = (String) mapStyle.get("iconUrl");
        this.iconData = (String) mapStyle.get("iconData");
        this.zIndexOffset = (Integer) mapStyle.get("zIndexOffset");

        Object weightValue = mapStyle.get("weight");
        if (weightValue != null ) {
            this.weight = Double.parseDouble(weightValue.toString());
        }
        // this.weight = (Double) mapStyle.get("weight");

        this.dashArray = (String) mapStyle.get("dashArray");
        this.fillColor = (String) mapStyle.get("fillColor");

        Object fillOpacityValue = mapStyle.get("fillOpacity");
        if (fillOpacityValue != null ) {
            this.fillOpacity = Double.parseDouble(fillOpacityValue.toString());
        }
        // this.fillOpacity = (Double) mapStyle.get("fillOpacity");

        this.fill = (Boolean) mapStyle.get("fill");
        this.stroke = (Boolean) mapStyle.get("stroke");
    }
}
