package io.basc.framework.orm;

public class DefaultEntityMetadata implements EntityMetadata {
	private final Class<?> entityClass;
	private final ObjectRelationalResolver objectRelationalResolver;

	public DefaultEntityMetadata(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass) {
		this.objectRelationalResolver = objectRelationalResolver;
		this.entityClass = entityClass;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	@Override
	public String getName() {
		return objectRelationalResolver.getName(entityClass);
	}

	@Override
	public String getCharsetName() {
		return objectRelationalResolver.getCharsetName(entityClass);
	}

	@Override
	public String getComment() {
		return objectRelationalResolver.getComment(entityClass);
	}
}
