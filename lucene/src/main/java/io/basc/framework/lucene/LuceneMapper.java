package io.basc.framework.lucene;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.lucene.annotation.LuceneField;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RepositoryMapper;
import io.basc.framework.value.Value;

public interface LuceneMapper extends RepositoryMapper, LuceneResolver, ObjectMapper<Document, LuceneException>,
		StructureRegistry<EntityStructure<? extends Property>> {

	@Override
	public default EntityStructure<? extends Property> getStructure(Class<?> entityClass) {
		return RepositoryMapper.super.getStructure(entityClass);
	}

	Query parseQuery(Conditions conditions);

	Query parseQuery(Document document);

	Sort parseSort(EntityStructure<? extends Property> structure, List<? extends OrderColumn> orders);

	default Document wrap(Document document, Object instance) {
		return wrap(document, instance, getFields(instance.getClass()).accept((field) -> {
			return field.isAnnotationPresent(LuceneField.class) || Value.isBaseType(field.getGetter().getType());
		}).all());
	}

	Document wrap(Document document, Object instance, Fields fields);

	Document wrap(Document document, EntityStructure<? extends Property> structure, Object instance);

	default Document createDocument(Object instance) {
		return wrap(new Document(), instance);
	}

	<T> void mapping(Document document, EntityStructure<? extends Property> structure, T entity);

	default <T> void mapping(Document document, T instance) {
		mapping(document, getStructure(instance.getClass()), instance);
	}
}
