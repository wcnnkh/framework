package io.basc.framework.context.jaxrs;

import java.io.IOException;
import java.util.Set;

import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;

@Provider
public class JaxrsContextResolver implements ContextResolverExtend {

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory,
			ContextResolver chain) throws IOException {
		Set<String> names = metadataReader.getAnnotationMetadata().getAnnotationTypes();
		for (String name : names) {
			if (name.startsWith("javax.ws.rs.")) {
				return true;
			}
		}
		return ContextResolverExtend.super.match(metadataReader, metadataReaderFactory, chain);
	}
}
