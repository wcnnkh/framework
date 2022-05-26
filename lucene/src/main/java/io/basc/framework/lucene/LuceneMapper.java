package io.basc.framework.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.orm.repository.RepositoryMapper;

public interface LuceneMapper extends RepositoryMapper, LuceneResolver, ObjectMapper<Document, LuceneException>,
		StructureRegistry<EntityStructure<? extends Property>> {

	@Override
	public default EntityStructure<? extends Property> getStructure(Class<?> entityClass) {
		return RepositoryMapper.super.getStructure(entityClass);
	}
	
	Query parseQuery(Conditions conditions);

	Query parseQuery(Document document);

	Sort parseSort(EntityStructure<? extends Property> structure, List<? extends OrderColumn> orders);

	void write(Object parameter, ParameterDescriptor parameterDescriptor, Document target);
	
	void reverseTransform(RepositoryColumn source, org.apache.lucene.document.Document target) throws LuceneException;

	default void reverseTransform(Collection<? extends RepositoryColumn> source, org.apache.lucene.document.Document target) throws LuceneException{
		for(RepositoryColumn column : source) {
			reverseTransform(column, target);
		}
	}
	
	default Document createDocument(Object instance) {
		return invert(instance, Document.class);
	}
}
