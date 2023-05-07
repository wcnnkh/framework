package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.ObjectMapper;

public interface EntityMapper extends ObjectMapper, EntityMappingResolver {

	@Override
	default boolean isEntity(Class<?> type) {
		return ObjectMapper.super.isEntity(type);
	}

	@Override
	default EntityMapping<? extends Property> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> mapping = ObjectMapper.super.getMapping(entityClass);
		return new DefaultEntityMapping<>(entityClass, this, mapping, (e) -> new DefaultProperty(e, entityClass, this));
	}
}
