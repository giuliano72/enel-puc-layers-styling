package com.enel.layersstyling.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "layer_rule")
public class LayerRule {

    @Id
    @GeneratedValue()
    private Long id;

    @Column
    private String layerGroup;

    @Column
    private String environment;

    @Column
    private Boolean linked;

    @Column(columnDefinition="TEXT")
    private String value;

    @ManyToMany(
            targetEntity=LayerRuleCriteria.class,
            cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    )
    @JoinTable(
            name="rules_criteria",
            joinColumns=@JoinColumn(name="rule_id"),
            inverseJoinColumns=@JoinColumn(name="criteria_id")
    )
    private List<LayerRuleCriteria> criteria;

    public LayerRule() {}

    public LayerRule(Map<String, Object> mapLayerRule) {
        this.id = (Long) mapLayerRule.get("id");

        this.layerGroup = (String) mapLayerRule.get("layerGroup");
        this.environment = (String) mapLayerRule.get("environment");
        this.linked = (Boolean) mapLayerRule.get("linked");

        Object valueObject = mapLayerRule.get("value");
        if (valueObject != null) {

            if (valueObject instanceof String) {
               this.value = (String) valueObject;

            } else {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    this.value = mapper.writer().writeValueAsString(valueObject);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        this.criteria = new ArrayList<>();
        ArrayList<Map<String, Object>> mapCriteriaList = (ArrayList<Map<String, Object>>) mapLayerRule.get("criteria");
        if (mapCriteriaList != null) {
            mapCriteriaList.forEach(mapCriteria -> {

                String ruleRef = (String) mapCriteria.get("ruleRef");
                if (ruleRef != null) {
                    this.criteria.add(new LayerRuleRefCriteria(mapCriteria) );

                } else {
                    this.criteria.add(new LayerRuleCriteriaImpl(mapCriteria) );
                }
            });
        }
    }
}
