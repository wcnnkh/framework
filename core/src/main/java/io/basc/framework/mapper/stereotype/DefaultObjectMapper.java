package io.basc.framework.mapper.stereotype;

import java.util.TreeMap;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.factory.support.DefaultPropertiesTransformerFactory;

public class DefaultObjectMapper extends DefaultPropertiesTransformerFactory<ConversionException>
		implements ObjectMapper, MappingRegistry {
	private TreeMap<Class<?>, Mapping<?>> mappingMap;
	@Nullable
	private MappingFactory mappingFactory;

	public MappingFactory getMappingFactory() {
		return mappingFactory;
	}

	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
	}

	@Override
	public Properties getProperties(Object transform, TypeDescriptor typeDescriptor) {
		Properties properties = super.getProperties(transform, typeDescriptor);
		if (properties == null) {
			Mapping<?> mapping = getMapping(transform.getClass());
			if (mapping != null) {
				properties = new MappingProperties(mapping, transform);
			}
		}
		return properties;
	}

	@Override
	public Mapping<? extends FieldDescriptor> getMapping(Class<?> entityClass) {
		Mapping<?> mapping = get(entityClass, mappingMap);
		if (mapping == null) {
			synchronized (this) {
				mapping = get(entityClass, mappingMap);
				if (mapping == null) {
					mapping = mappingFactory.getMapping(entityClass);
					this.mappingMap = register(entityClass, mapping, mappingMap);
				}
			}
		}
		return mapping;
	}

	@Override
	public boolean isMappingRegistred(Class<?> entityClass) {
		return get(entityClass, mappingMap) != null;
	}

	@Override
	public void registerMapping(Class<?> entityClass, Mapping<? extends FieldDescriptor> mapping) {
		synchronized (this) {
			this.mappingMap = register(entityClass, mapping, mappingMap);
		}
	}
}
