package io.basc.framework.mapper.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.basc.framework.core.Members;
import io.basc.framework.mapper.ObjectMapping;
import io.basc.framework.mapper.reflect.DefaultFieldsGenerator;
import io.basc.framework.util.element.Elements;

public final class DefaultObjectMapping extends ObjectMapping<DefaultField, DefaultObjectMapping> {
	private final Function<? super ObjectMapping<DefaultField, DefaultObjectMapping>, ? extends DefaultObjectMapping> objectMappingDecorator = (
			mapping) -> new DefaultObjectMapping(mapping);

	public DefaultObjectMapping(Class<?> source,
			Function<? super Class<?>, ? extends Elements<DefaultField>> processor) {
		super(source, processor);
	}

	private DefaultObjectMapping(Members<DefaultField> members) {
		super(members);
	}

	@Override
	public final Function<? super ObjectMapping<DefaultField, DefaultObjectMapping>, ? extends DefaultObjectMapping> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}

	private volatile Map<String, DefaultField> fieldMap;

	private void init() {
		if (fieldMap == null) {
			synchronized (this) {
				if (fieldMap == null) {
					List<DefaultField> list = super.getElements().toList();
					if (list.isEmpty()) {
						fieldMap = Collections.emptyMap();
						return;
					}

					fieldMap = new LinkedHashMap<>(list.size());
					for (DefaultField field : list) {
						DefaultField cacheField = fieldMap.get(field.getName());
						if (cacheField == null) {
							cacheField = new DefaultField(field);
							fieldMap.put(field.getName(), cacheField);
						} else {
							cacheField
									.setAliasNames(Elements.concat(cacheField.getAliasNames(), field.getAliasNames()));
							cacheField.setGetters(Elements.concat(cacheField.getGetters(), field.getGetters()));
							cacheField.setSetters(Elements.concat(cacheField.getSetters(), field.getSetters()));
						}
					}
				}
			}
		}
	}

	@Override
	public Elements<DefaultField> getElements() {
		init();
		return Elements.of(fieldMap.values());
	}

	@Override
	public Elements<DefaultField> getElements(String name) {
		init();
		DefaultField field = fieldMap.get(name);
		return field == null ? Elements.empty() : Elements.singleton(field);
	}

	public static DefaultObjectMapping getMapping(Class<?> sourceClass) {
		return new DefaultObjectMapping(sourceClass, DefaultFieldsGenerator.DEFAULT);
	}
}
