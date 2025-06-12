package com.enel.layersstyling.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "map_layer_style_attribute")
public class MapLayerStyleAttribute {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String value;

    public MapLayerStyleAttribute() {

    }

    public MapLayerStyleAttribute(Map<String, Object> mapAttribute) {
        this.id = (Long) mapAttribute.get("id");
        this.name = (String) mapAttribute.get("name");
        this.value = (String) mapAttribute.get("value");
    }
}
