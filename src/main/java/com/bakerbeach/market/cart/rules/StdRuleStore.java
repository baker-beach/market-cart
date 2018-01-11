package com.bakerbeach.market.cart.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.Rule.Type;
import com.bakerbeach.market.cart.api.model.RuleInstance;
import com.bakerbeach.market.cart.api.model.RuleStore;
import com.bakerbeach.market.cart.api.model.RuleTmpl;

public class StdRuleStore implements RuleStore {
	protected static final Logger log = LoggerFactory.getLogger(StdRuleStore.class);

	protected RuleStoreDao ruleStoreDao;

	public StdRuleStore(RuleStoreDao ruleStoreDao) {
		this.ruleStoreDao = ruleStoreDao;
	}

	private RuleInstance getInstance(RuleTmpl tmpl) throws Exception {
		String clazz = tmpl.getClazz();
		RuleInstance inst = (RuleInstance) Class.forName(clazz).newInstance();
		inst.setId(tmpl.getId());
		inst.setType(tmpl.getType());
		inst.setStart(tmpl.getStart());
		inst.setEnd(tmpl.getEnd());
		inst.setIntentions(tmpl.getIntentions());
		inst.setMaxIndividualUse(tmpl.getMaxIndividualUse());
		inst.setEmails(tmpl.getEmails());
		inst.setData(tmpl.getData());
		inst.setNewsletterSubscription(tmpl.getNewsletterSubscription());

		inst.init();

		return inst;
	}

	@Override
	public RuleInstance instanceById(String id) {
		try {
			RuleTmplImpl tmpl = ruleStoreDao.ruleTmplById(id);
			RuleInstance inst = getInstance(tmpl);
			return inst;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public RuleInstance instanceByCode(String code) {
		try {
			RuleMapping mapping = ruleStoreDao.ruleMappingByCode(code);
			RuleTmplImpl tmpl = ruleStoreDao.ruleTmplById(mapping.getRuleId());
			RuleInstance inst = getInstance(tmpl);
			return inst;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public Collection<RuleInstance> commonInstances() {
		Collection<RuleInstance> instances = new ArrayList<>();
		List<RuleTmpl> templates = ruleStoreDao.ruleTmplByType(Type.COMMON);
		templates.forEach(t -> {
			try {
				instances.add(getInstance(t));
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		});

		return instances;
	}

}
