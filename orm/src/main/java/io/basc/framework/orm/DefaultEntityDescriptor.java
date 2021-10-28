package io.basc.framework.orm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.mapper.Fields;

public class DefaultEntityDescriptor<T> extends EntityMetadataWrapper<EntityMetadata>
		implements EntityDescriptor<PropertyMetadata> {
	private final Class<?> entityClass;
	private final ObjectRelationalProcessor objectRelationalProcessor;
	private final Fields fields;

	public DefaultEntityDescriptor(ObjectRelationalProcessor objectRelationalProcessor, Class<?> entityClass,
			Fields fields) {
		super(objectRelationalProcessor.resolveMetadata(entityClass));
		this.objectRelationalProcessor = objectRelationalProcessor;
		this.entityClass = entityClass;
		this.fields = fields;
	}

	public ObjectRelationalProcessor getobjectRelationalProcessor() {
		return objectRelationalProcessor;
	}

	public Fields getFields() {
		return fields;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public List<PropertyMetadata> getProperties() {
		return stream().collect(Collectors.toList());
	}

	@Override
	public Stream<PropertyMetadata> stream() {
		return getobjectRelationalProcessor().map(entityClass, fields).map((p) -> p);
	}

}
