package io.basc.framework.lucene;

import org.apache.lucene.document.Document;

import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.collections.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DocumentProperties implements Properties {
	private final Document document;
	private final LuceneResolver luceneResolver;

	@Override
	public Elements<Property> getElements() {
		return Elements.of(document).map((field) -> new DocumentProperty(field, document, luceneResolver));
	}
}
