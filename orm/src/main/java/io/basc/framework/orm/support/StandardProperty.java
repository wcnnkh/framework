package io.basc.framework.orm.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.Property;

public class StandardProperty extends StandardPropertyMetadata implements Property {
	private Class<?> entityClass;
	private Collection<String> aliasNames;
	private Collection<Range<Double>> numberRanges;
	private boolean version;
	private boolean increment;
	private Field field;

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

	public Collection<Range<Double>> getNumberRanges() {
		if (numberRanges == null) {
			numberRanges = new ArrayList<>(4);
		}
		return numberRanges;
	}

	public void setNumberRanges(Collection<Range<Double>> numberRanges) {
		this.numberRanges = numberRanges;
	}

	public boolean isVersion() {
		return version;
	}

	public void setVersion(boolean version) {
		this.version = version;
	}

	public boolean isIncrement() {
		return increment;
	}

	public void setIncrement(boolean increment) {
		this.increment = increment;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
