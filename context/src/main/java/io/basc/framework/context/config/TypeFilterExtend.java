package io.basc.framework.context.config;

import java.io.IOException;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;

/**
 * TypeFilter扩展
 * 
 * @author wcnnkh
 *
 */
public interface TypeFilterExtend {
	boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory, TypeFilter chain)
			throws IOException;
}
