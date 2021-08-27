package io.basc.framework.orm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultEntityStructure implements EntityStructure<Property> {
	private final Class<?> entityClass;
	private final ObjectRelationalMapping objectRelationalMapping;

	public DefaultEntityStructure(Class<?> entityClass,
			ObjectRelationalMapping objectRelationalMapping) {
		this.entityClass = entityClass;
		this.objectRelationalMapping = objectRelationalMapping;
	}

	@Override
	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public String getName() {
		return objectRelationalMapping.getName(entityClass);
	}

	@Override
	public List<Property> getRows() {
		return stream().collect(Collectors.toList());
	}

	@Override
	public Stream<Property> stream() {
		return objectRelationalMapping
				.getFields(entityClass)
				.streamAll()
				.map((field) -> new DefaultProperty(field,
						objectRelationalMapping));
	}

}
