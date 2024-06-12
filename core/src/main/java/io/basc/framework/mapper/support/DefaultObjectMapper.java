package io.basc.framework.mapper.support;

import java.util.Map;
import java.util.TreeMap;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.mapper.stereotype.MappingFactory;
import io.basc.framework.mapper.stereotype.MappingProperties;
import io.basc.framework.mapper.stereotype.MappingRegistry;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.factory.support.DefaultPropertiesTransformerFactory;
import io.basc.framework.transform.map.MapProperties;

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

	public DefaultObjectMapper() {
		registerPropertiesTransformer(Map.class, (obj, type) -> new MapProperties(obj, type, this));
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
