package io.basc.framework.websocket.context;

import java.io.IOException;
import java.util.Set;

import io.basc.framework.beans.factory.annotation.Component;
import io.basc.framework.context.config.TypeFilterExtend;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;

@Component
class WebSocketContextResolver implements TypeFilterExtend {

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory, TypeFilter chain)
			throws IOException {
		Set<String> names = metadataReader.getAnnotationMetadata().getAnnotationTypes();
		for (String name : names) {
			if (name.startsWith("javax.websocket.")) {
				return true;
			}
		}
		return chain.match(metadataReader, metadataReaderFactory);
	}
}
