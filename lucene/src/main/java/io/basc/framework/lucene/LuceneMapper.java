package io.basc.framework.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Elements;

public interface LuceneMapper extends EntityMapper, LuceneResolver {

	Query parseQuery(Elements<Condition> conditions);

	Query parseQuery(Document document);

	Sort parseSort(Mapping<? extends Property> structure,
			Elements<? extends io.basc.framework.data.repository.Sort> orders);

	default Document createDocument(Object instance) {
		return invert(instance, Document.class);
	}
}
