package io.basc.framework.orm.repository;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;
import io.basc.framework.util.StringUtils;

public class DefaultRepositoryMapper extends DefaultObjectRelationalMapper
		implements RepositoryMapper {
	public static final RepositoryMapper DEFAULT = Sys.env.getServiceLoader(
			RepositoryMapper.class).first(() -> new DefaultRepositoryMapper());

	private ConditionKeywords conditionKeywords;
	private RelationshipKeywords relationshipKeywords;

	public ConditionKeywords getConditionKeywords() {
		return conditionKeywords == null ? RepositoryMapper.super
				.getConditionKeywords() : conditionKeywords;
	}

	public void setConditionKeywords(ConditionKeywords conditionKeywords) {
		this.conditionKeywords = conditionKeywords;
	}

	public RelationshipKeywords getRelationshipKeywords() {
		return relationshipKeywords == null ? RepositoryMapper.super
				.getRelationshipKeywords() : relationshipKeywords;
	}

	public void setRelationshipKeywords(
			RelationshipKeywords relationshipKeywords) {
		this.relationshipKeywords = relationshipKeywords;
	}
	
	@Override
	public String getRelationship(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		String relationship = RepositorySetting.getLocalRelationship().get(descriptor.getName());
		if(StringUtils.isNotEmpty(relationship)){
			return relationship;
		}
		return super.getRelationship(entityClass, descriptor);
	}
	
	@Override
	public String getCondition(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		String condition = RepositorySetting.getLocalConditions().get(descriptor.getName());
		if(StringUtils.isNotEmpty(condition)){
			return condition;
		}
		return super.getCondition(entityClass, descriptor);
	}
}
