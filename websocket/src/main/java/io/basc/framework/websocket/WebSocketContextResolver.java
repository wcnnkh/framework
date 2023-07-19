package io.basc.framework.websocket;

import java.io.IOException;
import java.util.Set;

import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;

@ConditionalOnParameters
public class WebSocketContextResolver implements ContextResolverExtend {
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory,
			ContextResolver chain) throws IOException {
		Set<String> names = metadataReader.getAnnotationMetadata().getAnnotationTypes();
		for (String name : names) {
			if (name.startsWith("javax.websocket.")) {
				return true;
			}
		}
		return ContextResolverExtend.super.match(metadataReader, metadataReaderFactory, chain);
	}
}
