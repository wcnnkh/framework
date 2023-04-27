package io.basc.framework.orm;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.mapper.DefaultField;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;

public class DefaultProperty extends DefaultField implements Property {
	protected Boolean autoIncrement;
	protected String charsetName;
	protected String comment;
	protected Boolean entity;
	protected Boolean increment;
	protected Boolean nullable;
	protected Collection<Range<Double>> numberRanges;
	protected ObjectRelationalResolver objectRelationalResolver;
	protected Boolean primaryKey;
	protected Boolean unique;
	protected Boolean version;

	public DefaultProperty() {
	}

	public DefaultProperty(DefaultField field) {
		super(field);
	}

	public DefaultProperty(DefaultProperty property) {
		super(property);
		this.autoIncrement = property.autoIncrement;
		this.charsetName = property.charsetName;
		this.comment = property.comment;
		this.entity = property.entity;
		this.increment = property.increment;
		this.numberRanges = property.numberRanges;
		this.objectRelationalResolver = property.objectRelationalResolver;
		this.primaryKey = property.primaryKey;
		this.unique = property.unique;
		this.version = property.version;
	}

	public DefaultProperty(Field field, ObjectRelationalResolver objectRelationalResolver) {
		super(field);
		this.objectRelationalResolver = objectRelationalResolver;
	}

	public DefaultProperty(Field field) {
		super(field);
	}

	@Override
	public DefaultProperty clone() {
		return new DefaultProperty(this);
	}

	public Collection<String> getAliasNames() {
		if (aliasNames == null && objectRelationalResolver != null) {
			if (isSupportSetter()) {
				return objectRelationalResolver.getAliasNames(getDeclaringClass(), getSetter());
			}

			if (isSupportSetter()) {
				return objectRelationalResolver.getAliasNames(getDeclaringClass(), getGetter());
			}
		}
		return super.getAliasNames();
	}

	public String getCharsetName() {
		if (charsetName == null && objectRelationalResolver != null) {
			if (isSupportGetter()) {
				return objectRelationalResolver.getCharsetName(getDeclaringClass(), getGetter());
			}

			if (isSupportSetter()) {
				return objectRelationalResolver.getCharsetName(getDeclaringClass(), getSetter());
			}
		}
		return charsetName;
	}

	public String getComment() {
		if (comment == null && objectRelationalResolver != null) {
			if (isSupportGetter()) {
				return objectRelationalResolver.getComment(getDeclaringClass(), getGetter());
			}

			if (isSupportSetter()) {
				return objectRelationalResolver.getComment(getDeclaringClass(), getSetter());
			}
		}
		return comment;
	}

	@Override
	public DefaultProperty getParent() {
		Field field = super.getParent();
		if (field == null) {
			return null;
		}

		if (field instanceof DefaultProperty) {
			return (DefaultProperty) field;
		}
		return new DefaultProperty(field, this.objectRelationalResolver);
	}

	@Override
	public void setParent(Field parent) {
		if (parent instanceof DefaultProperty) {
			setParent((DefaultProperty) parent);
			return;
		}

		DefaultProperty property = parent == null ? null : new DefaultProperty(parent, this.objectRelationalResolver);
		setParent(property);
	}

	public void setParent(DefaultProperty parent) {
		super.setParent(parent);
	}

	public String getName() {
		if (StringUtils.isEmpty(name) && objectRelationalResolver != null) {
			String name = null;
			if (isSupportGetter()) {
				name = objectRelationalResolver.getName(getDeclaringClass(), getGetter());
			}

			if (isSupportSetter()) {
				name = objectRelationalResolver.getName(getDeclaringClass(), getSetter());
			}

			if (StringUtils.isNotEmpty(name)) {
				if (hasParent() && this.nameNestingDepth > 0) {
					StringBuilder sb = new StringBuilder();
					Iterator<Field> parents = parents().reverse().iterator();
					int i = 0;
					while (parents.hasNext() && (i++ < this.nameNestingDepth)) {
						Field parent = parents.next();
						sb.append(parent.getName());
						sb.append(this.nameNestingConnector);
					}

					sb.append(name);
					return sb.toString();
				}

				return name;
			}
		}
		return super.getName();
	}

	public Collection<Range<Double>> getNumberRanges() {
		if (numberRanges == null && objectRelationalResolver != null) {
			if (isSupportSetter()) {
				return objectRelationalResolver.getNumberRanges(getDeclaringClass(), getSetter());
			}

			if (isSupportGetter()) {
				return objectRelationalResolver.getNumberRanges(getDeclaringClass(), getGetter());
			}
		}
		return numberRanges;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public <V, E extends Throwable> V getValueByNames(Processor<? super String, ? extends V, ? extends E> processor)
			throws E {
		V value = processor.process(getName());
		if (value != null) {
			return value;
		}

		Collection<String> names = getAliasNames();
		if (!CollectionUtils.isEmpty(names)) {
			for (String name : names) {
				value = processor.process(name);
				if (value != null) {
					return value;
				}
			}
		}

		value = super.getValueByNames(processor);
		if (value != null) {
			return value;
		}
		return null;
	}

	public boolean isAutoIncrement() {
		if (autoIncrement == null && objectRelationalResolver != null) {
			if (isSupportSetter()) {
				return getObjectRelationalResolver().isAutoIncrement(getDeclaringClass(), getSetter());
			}

			if (isSupportGetter()) {
				return getObjectRelationalResolver().isAutoIncrement(getDeclaringClass(), getGetter());
			}
		}
		return autoIncrement;
	}

	public boolean isEntity() {
		if (entity == null && objectRelationalResolver != null) {
			return (isSupportGetter() && objectRelationalResolver.isEntity(getDeclaringClass(), getGetter()))
					|| (isSupportSetter() && objectRelationalResolver.isEntity(getDeclaringClass(), getSetter()));
		}
		return entity == null ? false : entity;
	}

	public boolean isIncrement() {
		if (increment == null && objectRelationalResolver != null) {
			if (isSupportSetter()) {
				return objectRelationalResolver.isIncrement(getDeclaringClass(), getSetter());
			}

			if (isSupportGetter()) {
				return objectRelationalResolver.isIncrement(getDeclaringClass(), getGetter());
			}
		}
		return increment == null ? false : increment;
	}

	public boolean isNullable() {
		if (nullable == null && objectRelationalResolver != null) {
			if (isSupportSetter()) {
				return objectRelationalResolver.isNullable(getDeclaringClass(), getSetter());
			}

			if (isSupportGetter()) {
				return objectRelationalResolver.isNullable(getDeclaringClass(), getGetter());
			}
		}
		return nullable == null ? !isPrimaryKey() : nullable;
	}

	public boolean isPrimaryKey() {
		if (primaryKey == null && objectRelationalResolver != null) {
			if (isSupportGetter()) {
				return objectRelationalResolver.isPrimaryKey(getDeclaringClass(), getGetter());
			}

			if (isSupportSetter()) {
				return objectRelationalResolver.isPrimaryKey(getDeclaringClass(), getSetter());
			}
		}
		return primaryKey == null ? false : primaryKey;
	}

	public boolean isUnique() {
		if (unique == null && objectRelationalResolver != null) {
			if (isSupportGetter()) {
				return objectRelationalResolver.isUnique(getDeclaringClass(), getGetter());
			}

			if (isSupportSetter()) {
				return objectRelationalResolver.isUnique(getDeclaringClass(), getSetter());
			}
		}
		return unique == null ? false : unique;
	}

	public boolean isVersion() {
		if (version == null) {
			if (isSupportSetter()) {
				return objectRelationalResolver.isVersionField(getDeclaringClass(), getSetter());
			}

			if (isSupportGetter()) {
				return objectRelationalResolver.isVersionField(getDeclaringClass(), getGetter());
			}
		}
		return version == null ? false : version;
	}

	public void setAliasNames(Collection<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	public void setAutoIncrement(Boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setEntity(Boolean entity) {
		this.entity = entity;
	}

	public void setIncrement(Boolean increment) {
		this.increment = increment;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public void setNumberRanges(Collection<Range<Double>> numberRanges) {
		this.numberRanges = numberRanges;
	}

	public void setObjectRelationalResolver(ObjectRelationalResolver objectRelationalResolver) {
		this.objectRelationalResolver = objectRelationalResolver;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public void setVersion(Boolean version) {
		this.version = version;
	}

	@Override
	public boolean isSupportGetter() {
		if (super.isSupportGetter()) {
			if (objectRelationalResolver != null
					&& objectRelationalResolver.isIgnore(getDeclaringClass(), super.getGetter())) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isSupportSetter() {
		if (super.isSupportSetter()) {
			if (objectRelationalResolver != null
					&& objectRelationalResolver.isIgnore(getDeclaringClass(), super.getSetter())) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public DefaultProperty rename(String name) {
		DefaultProperty property = clone();
		property.setName(name);
		return property;
	}
}