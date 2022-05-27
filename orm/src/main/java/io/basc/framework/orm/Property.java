package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Named;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;

public class Property implements Named, Cloneable {
	private Collection<String> aliasNames;
	private Boolean autoIncrement;
	private String charsetName;
	private String comment;
	private ParameterDescriptor descriptor;

	private Boolean entity;
	private Field field;
	private Boolean increment;
	private String name;
	private Boolean nullable;
	private Collection<Range<Double>> numberRanges;
	private ObjectRelationalResolver objectRelationalResolver;
	private Property parentProperty;
	private Boolean primaryKey;
	private Class<?> sourceClass;
	private Boolean unique;
	private Boolean version;

	public Property(Property property) {
		this(property.parentProperty, property.objectRelationalResolver, property.name, property.aliasNames,
				property.numberRanges, property.version, property.increment, property.entity, property.autoIncrement,
				property.primaryKey, property.nullable, property.charsetName, property.comment, property.unique,
				property.field, property.descriptor, property.sourceClass);
	}

	protected Property(Property parentProperty, ObjectRelationalResolver objectRelationalResolver, String name,
			Collection<String> aliasNames, Collection<Range<Double>> numberRanges, Boolean version, Boolean increment,
			Boolean entity, Boolean autoIncrement, Boolean primaryKey, Boolean nullable, String charsetName,
			String comment, Boolean unique, Field field, ParameterDescriptor descriptor, Class<?> sourceClass) {
		this.parentProperty = parentProperty;
		this.objectRelationalResolver = objectRelationalResolver;
		this.name = name;
		this.aliasNames = aliasNames;
		this.numberRanges = numberRanges;
		this.version = version;
		this.increment = increment;
		this.entity = entity;
		this.autoIncrement = autoIncrement;
		this.primaryKey = primaryKey;
		this.nullable = nullable;
		this.charsetName = charsetName;
		this.comment = comment;
		this.unique = unique;
		this.field = field;
		this.descriptor = descriptor;
		this.sourceClass = sourceClass;
	}

	public Property(ObjectRelationalResolver objectRelationalResolver, ParameterDescriptor descriptor, Field field) {
		this(objectRelationalResolver, descriptor, field, null);
	}

	public Property(ObjectRelationalResolver objectRelationalResolver, ParameterDescriptor descriptor, Field field,
			String name) {
		Assert.isTrue(descriptor != null && objectRelationalResolver != null,
				"descriptor and objectRelationalResolver cannot both be empty");
		this.field = field;
		this.descriptor = descriptor;
		this.objectRelationalResolver = objectRelationalResolver;
		this.name = name;
	}

	@Override
	public Property clone() {
		return new Property(this);
	}

	public Collection<String> getAliasNames() {
		if (aliasNames == null) {
			return getObjectRelationalResolver().getAliasNames(getSourceClass(), getDescriptor());
		}
		return aliasNames;
	}

	public String getCharsetName() {
		if (charsetName == null) {
			return getObjectRelationalResolver().getCharsetName(getSourceClass(), getDescriptor());
		}
		return charsetName;
	}

	public String getComment() {
		if (comment == null) {
			return getObjectRelationalResolver().getComment(getSourceClass(), getDescriptor());
		}
		return comment;
	}

	public ParameterDescriptor getDescriptor() {
		if (descriptor == null) {
			return field.isSupportGetter() ? field.getGetter() : field.getSetter();
		}
		return descriptor;
	}

	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	public Field getField() {
		return field;
	}

	public String getName() {
		if (StringUtils.isEmpty(name)) {
			return getObjectRelationalResolver().getName(getSourceClass(), getDescriptor());
		}
		return name;
	}

	public Collection<Range<Double>> getNumberRanges() {
		if (numberRanges == null) {
			return getObjectRelationalResolver().getNumberRanges(getSourceClass(), getDescriptor());
		}
		return numberRanges;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public Property getParentProperty() {
		return parentProperty;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public <V, E extends Throwable> V getValueByNames(Processor<String, V, E> processor) throws E {
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

		Field field = getField();
		if (field != null) {
			value = field.getValueByNames(processor);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public boolean isAutoIncrement() {
		if (autoIncrement == null) {
			Boolean b = getObjectRelationalResolver().isAutoIncrement(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return autoIncrement;
	}

	public boolean isEntity() {
		if (entity == null) {
			Boolean b = getObjectRelationalResolver().isEntity(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return entity;
	}

	public boolean isIncrement() {
		if (increment == null) {
			Boolean b = getObjectRelationalResolver().isIncrement(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return increment;
	}

	public boolean isNullable() {
		if (primaryKey == null) {
			Boolean b = getObjectRelationalResolver().isNullable(getSourceClass(), getDescriptor());
			return b == null ? true : b;
		}
		return primaryKey;
	}

	public boolean isPrimaryKey() {
		if (primaryKey == null) {
			Boolean b = getObjectRelationalResolver().isPrimaryKey(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return primaryKey;
	}

	public boolean isUnique() {
		if (unique == null) {
			Boolean b = getObjectRelationalResolver().isUnique(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return unique;
	}

	public boolean isVersion() {
		if (version == null) {
			Boolean b = getObjectRelationalResolver().isVersionField(getSourceClass(), getDescriptor());
			return b == null ? false : b;
		}
		return version;
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

	public void setDescriptor(ParameterDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void setEntity(Boolean entity) {
		this.entity = entity;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setIncrement(Boolean increment) {
		this.increment = increment;
	}

	public void setName(String name) {
		Assert.isTrue(StringUtils.isNotEmpty(name) || getDescriptor() != null,
				"Name and Descriptor cannot both be empty");
		this.name = name;
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

	public void setParentProperty(Property parentProperty) {
		this.parentProperty = parentProperty;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public void setVersion(Boolean version) {
		this.version = version;
	}
}
