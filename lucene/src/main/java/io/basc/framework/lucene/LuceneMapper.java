package io.basc.framework.lucene;

import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.RepositoryMapper;

import org.apache.lucene.document.Document;

public interface LuceneMapper extends RepositoryMapper, LuceneResolver,
		ObjectMapper<Document, LuceneException>,
		StructureRegistry<EntityStructure<? extends Property>> {

	@Override
	public default EntityStructure<? extends Property> getStructure(
			Class<?> entityClass) {
		return RepositoryMapper.super.getStructure(entityClass);
	}
}
