package com.enel.layersstyling.entities;

import com.enel.layersstyling.LayerRuleCriteriaDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@Entity
@JsonDeserialize(using = LayerRuleCriteriaDeserializer.class)
@Table(name = "layer_rule_criteria")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="criteria_type", discriminatorType = DiscriminatorType.STRING)
public class LayerRuleCriteria {
    @Id
    @GeneratedValue()
    private Long id;

    @Column
    private Integer listOrder;

    @Column
    private String criteriaOperator; // 'AND' | 'OR'


    public LayerRuleCriteria() {}

    public LayerRuleCriteria(Long id, Integer listOrder, String criteriaOperator) {
        this.id = id;
        this.listOrder = listOrder;
        this.criteriaOperator = criteriaOperator;
    }

    public LayerRuleCriteria(Map<String, Object> mapCriteria) {
        this.id = (Long) mapCriteria.get("id");

        Object orderObject = mapCriteria.get("listOrder");
        if (orderObject != null) {
            this.listOrder = (Integer)orderObject;
        }

        this.criteriaOperator = (String) mapCriteria.get("criteriaOperator");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LayerRuleCriteria that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void update(LayerRuleCriteria layerRuleCriteria) {
        this.listOrder = layerRuleCriteria.listOrder;
        this.criteriaOperator = layerRuleCriteria.criteriaOperator;
    }
}
