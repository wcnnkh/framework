package io.basc.framework.lucene;

import java.util.Enumeration;

import org.apache.lucene.document.Document;

import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.CollectionUtils;

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
	public Parameter get(String name) throws LuceneException {
		String value = document.get(name);
		if (value == null) {
			return null;
		}
		return new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) throws LuceneException {
		document.removeField(parameter.getName());
		luceneResolver.resolve(parameter).forEach(document::add);
	}

}
