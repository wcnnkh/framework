package io.basc.framework.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.util.collection.Elements;

public interface LuceneMapper extends EntityMapper, LuceneResolver {
	Document createDocument(Operation operation, Elements<? extends Expression> columns);

	Query createQuery(Operation operation, Elements<? extends Condition> conditions);

	Sort createSort(Operation operation, Elements<? extends io.basc.framework.data.repository.Sort> orders);
}
