package io.basc.framework.orm.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.orm.EntityMetadata;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;

public class StandardEntityStructure<T extends Property> extends StandardEntityDescriptor<T>
		implements EntityStructure<T> {
	private Class<?> entityClass;
	private Collection<String> aliasNames;

	public StandardEntityStructure() {
	}

	public StandardEntityStructure(EntityMetadata entityMetadata) {
		super(entityMetadata);
	}

	public StandardEntityStructure(EntityStructure<T> entityStructure) {
		super(entityStructure);
		this.entityClass = entityStructure.getEntityClass();
		this.aliasNames = entityStructure.getAliasNames();
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public Collection<String> getAliasNames() {
		if (aliasNames == null) {
			aliasNames = new LinkedHashSet<>(4);
		}
		return aliasNames;
	}

	public void setAliasNames(Collection<String> aliasNames) {
		this.aliasNames = aliasNames;
	}
}
