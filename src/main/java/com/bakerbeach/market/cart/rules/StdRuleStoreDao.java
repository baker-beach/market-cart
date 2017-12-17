package com.bakerbeach.market.cart.rules;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.Rule.Type;
import com.bakerbeach.market.cart.api.model.RuleTmpl;

public class StdRuleStoreDao implements RuleStoreDao {
	protected static final Logger log = LoggerFactory.getLogger(StdRuleStoreDao.class);

	protected static final String DEFAULT_RULE_USE_COLLECTION_NAME = "rule_use";
	protected static final String DEFAULT_RULE_MAPPING_COLLECTION_NAME = "rule_mapping";
	protected static final String DEFAULT_RULE_TMPL_COLLECTION_NAME = "rule_tmpl";

	protected String ruleUseCollectionName = DEFAULT_RULE_USE_COLLECTION_NAME;
	protected String ruleMappingCollectionName = DEFAULT_RULE_MAPPING_COLLECTION_NAME;
	protected String ruleTmplCollectionName = DEFAULT_RULE_TMPL_COLLECTION_NAME;

	protected Datastore datastore;

	public StdRuleStoreDao(Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public RuleTmplImpl ruleTmplById(String id) {
		Query<RuleTmplImpl> query = ((AdvancedDatastore) datastore)
				.createQuery(ruleTmplCollectionName, RuleTmplImpl.class).field("id").equal(id);
		RuleTmplImpl ruleTmpl = query.get();
		
		return ruleTmpl;
	}

	@Override
	public List<RuleTmpl> ruleTmplByType(Type type) {
		List<RuleTmpl> list = new ArrayList<>();

		Query<RuleTmplImpl> query = ((AdvancedDatastore) datastore)
				.createQuery(ruleTmplCollectionName, RuleTmplImpl.class).field("type").equal(type);
		query.forEach(t -> {
			list.add(t);
		});
				
		return list;
	}

	@Override
	public RuleMapping ruleMappingByCode(String code) {
		Query<RuleMapping> query = ((AdvancedDatastore) datastore)
				.createQuery(ruleMappingCollectionName, RuleMapping.class).field("code").equal(code);
		RuleMapping mapping = query.get();

		return mapping;
	}

}
