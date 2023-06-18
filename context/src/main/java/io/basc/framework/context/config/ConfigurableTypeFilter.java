package io.basc.framework.context.config;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;

public class ConfigurableTypeFilter extends ConfigurableServices<TypeFilterExtend> implements TypeFilter {

	public ConfigurableTypeFilter() {
		super(TypeFilterExtend.class);
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		TypeFilterChain chain = new TypeFilterChain(getServices().iterator());
		return chain.match(metadataReader, metadataReaderFactory);
	}

}
