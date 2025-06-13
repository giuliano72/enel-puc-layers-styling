package com.enel.layersstyling.entities;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Entity
@DiscriminatorValue("LAYER_RULE_REF_CRITERIA")
public class LayerRuleRefCriteria extends LayerRuleCriteria {

    @Column
    private String ruleRef;

    public LayerRuleRefCriteria() {}

    public LayerRuleRefCriteria(Map<String, Object> mapCriteria) {
        super(mapCriteria);
        this.ruleRef = (String) mapCriteria.get("ruleRef");
    }

    public LayerRuleRefCriteria(Long id, Integer listOrder, String criteriaOperator, String ruleRef) {
        super(id, listOrder, criteriaOperator);
        this.ruleRef = ruleRef;
    }

    public void update(LayerRuleCriteria layerRuleCriteria) {
        super.update(layerRuleCriteria);

        if (layerRuleCriteria instanceof LayerRuleRefCriteria) {
            this.ruleRef = ((LayerRuleRefCriteria) layerRuleCriteria).ruleRef;
        }
    }
}
