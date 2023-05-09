package io.basc.framework.data.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.basc.framework.util.Assert;
import lombok.Data;

@Data
public class Conditions implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Condition condition;
	private final List<WithCondition> withs;

	public Conditions(Condition condition, List<WithCondition> withs) {
		Assert.requiredArgument(condition != null, "condition");
		this.condition = condition;
		this.withs = withs == null ? Collections.emptyList() : Collections.unmodifiableList(withs);
	}

	public Condition getCondition() {
		return condition;
	}

	public List<WithCondition> getWiths() {
		return withs;
	}
}
