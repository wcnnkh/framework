package io.basc.framework.lucene;

import java.util.Enumeration;

import org.apache.lucene.document.Document;

import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

public class DocumentAccess implements ObjectAccess<LuceneException> {
	private final Document document;
	private final LuceneResolver luceneResolver;

	public DocumentAccess(Document document, LuceneResolver luceneResolver) {
		this.document = document;
		this.luceneResolver = luceneResolver;
	}

	@Override
	public Enumeration<String> keys() throws LuceneException {
		return CollectionUtils.toEnumeration(this.document.getFields().stream().map((e) -> e.name()).iterator());
	}

	@Override
	public Value get(String name) throws LuceneException {
		String value = document.get(name);
		return new StringValue(value);
	}

	@Override
	public void set(String name, Value value) throws LuceneException {
		document.removeField(name);
		luceneResolver.resolve(new Parameter(name, value)).forEach((e) -> document.add(e));
	}

}
