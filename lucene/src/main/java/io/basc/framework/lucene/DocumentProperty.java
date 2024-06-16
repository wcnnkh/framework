package io.basc.framework.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import io.basc.framework.execution.param.SimpleParameter;
import io.basc.framework.transform.Property;

public class DocumentProperty extends IndexableFieldParameter implements Property {
	private final Document document;
	private final LuceneResolver luceneResolver;

	public DocumentProperty(IndexableField indexableField, Document document, LuceneResolver luceneResolver) {
		super(indexableField);
		this.document = document;
		this.luceneResolver = luceneResolver;
	}

	@Override
	public void setValue(Object value) throws UnsupportedOperationException {
		document.removeField(getName());
		SimpleParameter parameter = new SimpleParameter(this);
		parameter.setValue(value);
		luceneResolver.resolve(parameter).forEach(document::add);
	}
}
