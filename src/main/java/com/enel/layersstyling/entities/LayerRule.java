package com.enel.layersstyling.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Column
    private Integer listOrder;

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
    @OrderBy("listOrder")
    private List<LayerRuleCriteria> criteria;

    public LayerRule() {}

    public LayerRule(Map<String, Object> mapLayerRule) {
        this.id = (Long) mapLayerRule.get("id");

        this.layerGroup = (String) mapLayerRule.get("layerGroup");
        this.environment = (String) mapLayerRule.get("environment");
        this.linked = (Boolean) mapLayerRule.get("linked");

        Object orderObject = mapLayerRule.get("listOrder");
        if (orderObject != null) {
            this.listOrder = (Integer)orderObject;
        }

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

            for (int idx = 0; idx < mapCriteriaList.size(); idx++) {
                Map<String, Object> mapCriteria = mapCriteriaList.get(idx);
                String ruleRef = (String) mapCriteria.get("ruleRef");
                if (ruleRef != null) {
                    LayerRuleRefCriteria layerRuleRefCriteria = new LayerRuleRefCriteria(mapCriteria);
                    if (layerRuleRefCriteria.getListOrder() == null) {
                        layerRuleRefCriteria.setListOrder(idx);
                    }
                    this.criteria.add(layerRuleRefCriteria);

                } else {
                    LayerRuleCriteriaImpl layerRuleCriteriaImpl = new LayerRuleCriteriaImpl(mapCriteria);
                    if (layerRuleCriteriaImpl.getListOrder() == null) {
                        layerRuleCriteriaImpl.setListOrder(idx);
                    }
                    this.criteria.add(layerRuleCriteriaImpl);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LayerRule layerRule)) return false;
        return Objects.equals(id, layerRule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void update(LayerRule layerRule, EntityManager entityManager) {
        this.layerGroup = layerRule.getLayerGroup();
        this.environment = layerRule.getEnvironment();
        this.linked = layerRule.getLinked();
        this.listOrder = layerRule.getListOrder();
        this.value = layerRule.getValue();

        if ((layerRule.getCriteria() != null) && !layerRule.getCriteria().isEmpty()) {

            // ADD NEWS LAYER RULE CRITERIA
            for (LayerRuleCriteria layerRuleCriteria : layerRule.getCriteria()){
                int index = this.criteria.indexOf(layerRuleCriteria);

                if (index != -1) {
                    System.out.println("layerRuleCriteria FOUND, updating layerRuleCriteria id: "+layerRuleCriteria.getId());
                    this.criteria.get(index).update(layerRuleCriteria);

                } else {
                    System.out.println("layerRuleCriteria NOT FOUND, new layerRuleCriteria id: "+layerRuleCriteria.getId());
                    entityManager.persist(layerRuleCriteria);
                    entityManager.flush();
                    this.criteria.add(layerRuleCriteria);
                }
            }

            // REMOVE OLDS LAYER RULE CRITERIA
            for (LayerRuleCriteria layerRuleCriteria : this.getCriteria()){
                int index = layerRule.criteria.indexOf(layerRuleCriteria);
                if (index == -1) {
                    this.getCriteria().remove(layerRuleCriteria);
                }
            }

            this.criteria.sort((o1, o2) -> {
                return o1.getListOrder().compareTo(o2.getListOrder());
            });

        } else {
            this.criteria.clear();
        }
    }
}
