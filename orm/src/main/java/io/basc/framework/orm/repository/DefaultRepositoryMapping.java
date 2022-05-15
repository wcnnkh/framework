package io.basc.framework.orm.repository;

import io.basc.framework.env.Sys;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;

public class DefaultRepositoryMapping extends DefaultObjectRelationalMapping
		implements RepositoryMapping {
	public static final RepositoryMapping DEFAULT = Sys.env.getServiceLoader(
			RepositoryMapping.class)
			.first(() -> new DefaultRepositoryMapping());

	private ConditionKeywords conditionKeywords;
	private RelationshipKeywords relationshipKeywords;

	public ConditionKeywords getConditionKeywords() {
		return conditionKeywords == null ? RepositoryMapping.super
				.getConditionKeywords() : conditionKeywords;
	}

	public void setConditionKeywords(ConditionKeywords conditionKeywords) {
		this.conditionKeywords = conditionKeywords;
	}

	public RelationshipKeywords getRelationshipKeywords() {
		return relationshipKeywords == null ? RepositoryMapping.super
				.getRelationshipKeywords() : relationshipKeywords;
	}

	public void setRelationshipKeywords(
			RelationshipKeywords relationshipKeywords) {
		this.relationshipKeywords = relationshipKeywords;
	}
}
