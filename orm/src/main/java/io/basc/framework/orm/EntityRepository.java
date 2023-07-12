package io.basc.framework.orm;

import io.basc.framework.data.repository.Repository;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntityRepository<T> extends Repository implements Cloneable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final EntityMapping<?> entityMapping;
	@NonNull
	private final Class<? extends T> entityClass;
	private T entity;

	public EntityRepository(@NonNull String name, EntityMapping<?> entityMapping, Class<? extends T> entityClass,
			@Nullable T entity) {
		super(name);
		Assert.requiredArgument(entityMapping != null, "entityMapping");
		Assert.requiredArgument(entityClass != null, "entityClass");
		this.entityMapping = entityMapping;
		this.entityClass = entityClass;
		this.entity = entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntityRepository<T> clone() throws UnsupportedException {
		try {
			return (EntityRepository<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedException(e);
		}
	}
}
