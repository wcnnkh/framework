package io.basc.framework.lucene;

import org.apache.lucene.document.Document;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class DocumentAccess implements ObjectAccess {
	private final Document document;
	private final LuceneResolver luceneResolver;
	private final TypeDescriptor typeDescriptor;

	public DocumentAccess(Document document, LuceneResolver luceneResolver, TypeDescriptor typeDescriptor) {
		this.document = document;
		this.luceneResolver = luceneResolver;
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public Elements<String> keys() throws LuceneException {
		return Elements.of(() -> this.document.getFields().stream().map((e) -> e.name()));
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
