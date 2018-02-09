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
	public RuleInstance instanceById(String shopCode, String id) {
		try {
			RuleTmplImpl tmpl = ruleStoreDao.ruleTmplById(shopCode, id);
			RuleInstance inst = getInstance(tmpl);
			return inst;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}
	
	@Override
	public RuleInstance instanceById(String id) {
		return instanceById(null, id);
	}
	
	@Override
	public RuleInstance instanceByCode(String shopCode, String code) {
		try {
			RuleMapping mapping = ruleStoreDao.ruleMappingByCode(shopCode, code);
			RuleTmplImpl tmpl = ruleStoreDao.ruleTmplById(shopCode, mapping.getRuleId());
			RuleInstance inst = getInstance(tmpl);
			return inst;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public RuleInstance instanceByCode(String code) {
		return instanceByCode(null, code);
	}

	@Override
	public Collection<RuleInstance> commonInstances(String shopCode) {
		Collection<RuleInstance> instances = new ArrayList<>();
		List<RuleTmpl> templates = ruleStoreDao.ruleTmplByType(shopCode, Type.COMMON);
		templates.forEach(t -> {
			try {
				instances.add(getInstance(t));
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		});

		return instances;
	}

	@Override
	public Collection<RuleInstance> commonInstances() {
		return commonInstances(null);
	}
}
