package io.basc.framework.orm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.mapper.Fields;

public class DefaultEntityStructure extends EntityMetadataWrapper<EntityMetadata> implements EntityStructure<Property> {
	private final Class<?> entityClass;
	private final ObjectRelationalProcessor objectRelationalProcessor;
	private final ObjectRelationalResolver objectRelationalResolver;
	private final Fields fields;

	public DefaultEntityStructure(ObjectRelationalResolver objectRelationalResolver,
			ObjectRelationalProcessor objectRelationalProcessor, Class<?> entityClass, Fields fields) {
		super(objectRelationalProcessor.resolveMetadata(entityClass));
		this.objectRelationalResolver = objectRelationalResolver;
		this.objectRelationalProcessor = objectRelationalProcessor;
		this.entityClass = entityClass;
		this.fields = fields;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
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
	public List<Property> getProperties() {
		return stream().collect(Collectors.toList());
	}

	@Override
	public Stream<Property> stream() {
		return getobjectRelationalProcessor().map(entityClass, fields).map((p) -> p);
	}

	@Override
	public Collection<String> getAliasNames() {
		return objectRelationalResolver.getAliasNames(entityClass);
	}

}
