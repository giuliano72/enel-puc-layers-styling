package com.enel.layersstyling.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Entity
@DiscriminatorValue("LAYER_RULE_CRITERIA")
public class LayerRuleCriteriaImpl extends LayerRuleCriteria {

    @Column
    private String field;

    @Column
    private String value;

    @Column
    private String fieldType; // 'integer' | 'string'

    @Column
    private String operator; // '=' | '!=' | 'IS_CONTAINED'

    public LayerRuleCriteriaImpl() {
        super();
    }

    public LayerRuleCriteriaImpl(Long id, Integer listOrder, String criteriaOperator, String field, String value, String fieldType, String operator) {
        super(id, listOrder, criteriaOperator);
        this.field = field;
        this.value = value;
        this.fieldType = fieldType;
        this.operator = operator;
    }

    public LayerRuleCriteriaImpl(Map<String, Object> mapCriteria) {
        super(mapCriteria);
        this.field = (String) mapCriteria.get("field");
        this.value = (String) mapCriteria.get("value");
        this.fieldType = (String) mapCriteria.get("fieldType");
        this.operator = (String) mapCriteria.get("operator");
    }

    public void update(LayerRuleCriteria layerRuleCriteria) {
        super.update(layerRuleCriteria);

        if (layerRuleCriteria instanceof LayerRuleCriteriaImpl) {
            this.field = ((LayerRuleCriteriaImpl) layerRuleCriteria).field;
            this.value = ((LayerRuleCriteriaImpl) layerRuleCriteria).value;
            this.fieldType = ((LayerRuleCriteriaImpl) layerRuleCriteria).fieldType;
            this.operator = ((LayerRuleCriteriaImpl) layerRuleCriteria).operator;
        }
    }
}
