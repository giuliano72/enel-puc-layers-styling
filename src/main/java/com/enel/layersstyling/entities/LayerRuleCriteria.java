package com.enel.layersstyling.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Entity
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

    public LayerRuleCriteria(Map<String, Object> mapCriteria) {
        this.id = (Long) mapCriteria.get("id");

        Object orderObject = mapCriteria.get("listOrder");
        if (orderObject != null) {
            this.listOrder = (Integer)orderObject;
        }

        this.criteriaOperator = (String) mapCriteria.get("criteriaOperator");
    }

}
