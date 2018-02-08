package com.bakerbeach.market.cart.rules;

import java.util.List;

import com.bakerbeach.market.cart.api.model.Rule.Type;
import com.bakerbeach.market.cart.api.model.RuleTmpl;

public interface RuleStoreDao {

	RuleTmplImpl ruleTmplById(String shopCode, String id);

	List<RuleTmpl> ruleTmplByType(String shopCode, Type type);

	RuleMapping ruleMappingByCode(String shopCode, String code);

}
