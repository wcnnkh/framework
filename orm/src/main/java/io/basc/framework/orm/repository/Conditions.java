package io.basc.framework.orm.repository;

import io.basc.framework.env.BascObject;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Conditions extends BascObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Condition condition;
	private final List<WithCondition> withs;

	public Conditions(Condition condition, List<WithCondition> withs) {
		Assert.requiredArgument(condition != null, "condition");
		this.condition = condition;
		this.withs = withs == null ? Collections.emptyList() : Collections
				.unmodifiableList(withs);
	}

	public Condition getCondition() {
		return condition;
	}

	public List<WithCondition> getWiths() {
		return withs;
	}

	/**
	 * 简单的构造
	 * 
	 * @param conditions
	 * @return
	 */
	public static Conditions build(List<Pair<String, Condition>> conditions) {
		return build(conditions.iterator());
	}

	public static Conditions build(Iterator<Pair<String, Condition>> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return null;
		}
		Pair<String, Condition> pair = iterator.next();
		Condition root = pair.getValue();
		List<WithCondition> list = new ArrayList<WithCondition>();
		while (iterator.hasNext()) {
			pair = iterator.next();
			list.add(new WithCondition(pair.getKey(), new Conditions(pair
					.getValue(), null)));
		}
		return new Conditions(root, list);
	}
}
