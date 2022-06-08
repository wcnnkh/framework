package io.basc.framework.lucene;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.mapper.Structure;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.RepositoryResolver;

public interface LuceneMapper
		extends RepositoryResolver, LuceneResolver, ObjectRelationalMapper<Document, LuceneException> {

	Query parseQuery(Conditions conditions);

	Query parseQuery(Document document);

	Sort parseSort(Structure<? extends Property> structure, List<? extends OrderColumn> orders);

	default Document createDocument(Object instance) {
		return invert(instance, Document.class);
	}
}
