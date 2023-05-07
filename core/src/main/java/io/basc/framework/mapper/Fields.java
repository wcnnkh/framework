package io.basc.framework.mapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.basc.framework.core.Members;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;

public final class Fields extends FieldMapping<Field, Fields> {
	private static final FieldsGenerator FIELDS_GENERATOR = new FieldsGenerator();

	private final Function<? super FieldMapping<Field, Fields>, ? extends Fields> objectMappingDecorator = (
			mapping) -> new Fields(mapping);

	public Fields(Class<?> source, Function<? super Class<?>, ? extends Elements<Field>> processor) {
		super(source, processor);
	}

	private Fields(Members<Field> members) {
		super(members);
	}

	@Override
	public final Function<? super FieldMapping<Field, Fields>, ? extends Fields> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}

	private volatile Map<String, DefaultField> fieldMap;

	private void init() {
		if (fieldMap == null) {
			synchronized (this) {
				if (fieldMap == null) {
					for (DefaultField field : super.getElements()) {
						DefaultField cacheField = fieldMap.get(field.getName());
						if (cacheField == null) {
							fieldMap.put(field.getName(), field);
						} else {

						}
					}
				}
			}
		}
	}

	@Override
	public Elements<Field> getElements() {
		init();
		return Elements.of(() -> fieldMap.values().stream().flatMap((e) -> e.stream()));
	}

	@Override
	public Elements<Field> getElements(String name) {
		init();
		List<DefaultField> fields = fieldMap.get(name);
		return CollectionUtils.isEmpty(fields) ? Elements.empty() : Elements.of(fields);
	}

	public static Fields getMapping(Class<?> sourceClass) {
		return new Fields(sourceClass, FIELDS_GENERATOR);
	}

}
