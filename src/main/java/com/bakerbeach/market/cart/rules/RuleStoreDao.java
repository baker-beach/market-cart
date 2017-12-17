package com.bakerbeach.market.cart.rules;

import java.util.List;

import com.bakerbeach.market.cart.api.model.Rule.Type;
import com.bakerbeach.market.cart.api.model.RuleTmpl;

public interface RuleStoreDao {

	RuleTmplImpl ruleTmplById(String id);

	List<RuleTmpl> ruleTmplByType(Type type);

	RuleMapping ruleMappingByCode(String code);

}
