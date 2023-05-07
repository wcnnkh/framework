package io.basc.framework.orm.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotationAttributes;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapperContext;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.support.EntityMappingResolverExtend;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;

public class AnnotationObjectRelationalResolverExtend
		implements EntityMappingResolverExtend, PlaceholderFormatAware {
	private PlaceholderFormat placeholderFormat;

	@Nullable
	public PlaceholderFormat getPlaceholderFormat() {
		return placeholderFormat;
	}

	@Override
	public void setPlaceholderFormat(@Nullable PlaceholderFormat placeholderFormat) {
		this.placeholderFormat = placeholderFormat;
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, EntityMappingResolver chain) {
		Ignore ignore = entityClass.getAnnotation(Ignore.class);
		if (ignore == null) {
			return chain.isIgnore(entityClass);
		}
		return true;
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Ignore ignore = descriptor.getTypeDescriptor().getAnnotation(Ignore.class);
		if (ignore == null) {
			return chain.isIgnore(entityClass, descriptor);
		}
		return true;
	}

	private String getAnnotationFeldName(AnnotatedElement annotatedElement) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils
				.getMergedAnnotationAttributes(annotatedElement, Named.class);
		if (annotationAttributes == null) {
			return null;
		}

		String name = annotationAttributes.getString("value");
		return StringUtils.isEmpty(name) ? null : name;
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		String name = getAnnotationFeldName(descriptor);
		if (StringUtils.isEmpty(name)) {
			return chain.getName(entityClass, descriptor);
		}
		return name;
	}

	private String[] getAnnotatedAlias(AnnotatedElement annotatedElement) {
		Alias alias = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Alias.class);
		return alias == null ? null : alias.value();
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		Collection<String> names = chain.getAliasNames(entityClass, descriptor);
		if (names == null) {
			names = new LinkedHashSet<String>();
		}
		String name = getAnnotationFeldName(descriptor);
		if (name != null) {
			names.add(name);
		}

		String[] aliasArray = getAnnotatedAlias(descriptor);
		if (aliasArray != null) {
			for (String alias : aliasArray) {
				if (StringUtils.isNotEmpty(alias)) {
					names.add(alias);
				}
			}
		}
		return names;
	}

	@Override
	public String getName(Class<?> entityClass, EntityMappingResolver chain) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(entityClass,
				Entity.class);
		if (annotationAttributes == null) {
			return chain.getName(entityClass);
		}

		String name = annotationAttributes.getString("name");
		if (StringUtils.isEmpty(name)) {
			return chain.getName(entityClass);
		}
		return name;
	}

	private String getEntityNameByAnnotatedElement(AnnotatedElement annotatedElement) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils
				.getMergedAnnotationAttributes(annotatedElement, Entity.class);
		if (annotationAttributes == null) {
			return null;
		}

		String name = annotationAttributes.getString("name");
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return name;
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, EntityMappingResolver chain) {
		Collection<String> list = chain.getAliasNames(entityClass);
		if (list == null) {
			list = new LinkedHashSet<String>(8);
		}
		String name = getEntityNameByAnnotatedElement(entityClass);
		if (StringUtils.isNotEmpty(name)) {
			list.add(name);
		}

		String[] aliasArray = getAnnotatedAlias(entityClass);
		if (aliasArray != null) {
			for (String alias : aliasArray) {
				if (StringUtils.isNotEmpty(alias)) {
					list.add(alias);
				}
			}
		}
		return list;
	}

	@Override
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		// TODO 为什么使用AnnotatedElementUtils.hasAnnotation无法获取到
		if (descriptor.isAnnotationPresent(PrimaryKey.class)) {
			return true;
		}

		Class<?> clazz = entityClass;
		while (clazz != null && clazz != Object.class) {
			Entity entity = AnnotatedElementUtils.getMergedAnnotation(clazz, Entity.class);
			if (entity != null) {
				for (String name : entity.primaryKeys()) {
					if (descriptor.getName().equals(name)) {
						return true;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	@Override
	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Nullable nullable = AnnotatedElementUtils.getMergedAnnotation(descriptor, Nullable.class);
		if (nullable == null) {
			return chain.isNullable(entityClass, descriptor);
		}
		return nullable.value();
	}

	@Override
	public boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor, Entity.class)) {
			return true;
		}
		return chain.isEntity(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(Class<?> entityClass, EntityMappingResolver chain) {
		Class<?> clazz = entityClass;
		while (clazz != null && clazz != Object.class) {
			if (AnnotatedElementUtils.hasAnnotation(clazz, Entity.class)) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return chain.isEntity(entityClass);
	}

	@Override
	public boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor, Version.class)) {
			return true;
		}
		return chain.isVersionField(entityClass, descriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		Collection<Range<Double>> ranges = chain.getNumberRanges(entityClass, descriptor);
		NumberRange range = AnnotatedElementUtils.getMergedAnnotation(descriptor, NumberRange.class);
		if (range != null) {
			if (ranges == null) {
				ranges = new LinkedHashSet<>(4);
				ranges.add(Range.closed(range.min(), range.max()));
			}
		}
		return ranges;
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor, AutoIncrement.class)) {
			return true;
		}

		return chain.isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public String getComment(Class<?> entityClass, EntityMappingResolver chain) {
		Entity entity = AnnotatedElementUtils.getMergedAnnotation(entityClass, Entity.class);
		if (entity != null && StringUtils.hasText(entity.comment())) {
			return entity.comment();
		}

		Comment comment = AnnotatedElementUtils.getMergedAnnotation(entityClass, Comment.class);
		if (comment != null && StringUtils.hasText(comment.value())) {
			return comment.value();
		}
		return chain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Comment comment = AnnotatedElementUtils.getMergedAnnotation(descriptor, Comment.class);
		if (comment != null && StringUtils.hasText(comment.value())) {
			return comment.value();
		}
		return chain.getComment(entityClass, descriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, EntityMappingResolver chain) {
		return Annotations.getCharsetName(entityClass, () -> chain.getCharsetName(entityClass));
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return Annotations.getCharsetName(descriptor, () -> chain.getCharsetName(entityClass, descriptor));
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor, Unique.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor, Increment.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		SortType sortType = AnnotatedElementUtils.getMergedAnnotation(descriptor, SortType.class);
		return sortType == null ? chain.getSort(entityClass, descriptor) : sortType.value();
	}

	@Override
	public String getCondition(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Condition condition = AnnotatedElementUtils.getMergedAnnotation(descriptor, Condition.class);
		return (condition == null || StringUtils.isEmpty(condition.value()))
				? chain.getCondition(entityClass, descriptor)
				: condition.value();
	}

	@Override
	public String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		Relationship relationship = AnnotatedElementUtils.getMergedAnnotation(descriptor, Relationship.class);
		return (relationship == null || StringUtils.isEmpty(relationship.value()))
				? chain.getRelationship(entityClass, descriptor)
				: relationship.value();
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Display display = AnnotatedElementUtils.getMergedAnnotation(entityClass, Display.class);
		if (display != null && StringUtils.equals(display.name(), descriptor.getName())) {
			return true;
		}

		display = AnnotatedElementUtils.getMergedAnnotation(descriptor, Display.class);
		if (display != null) {
			return true;
		}
		return EntityMappingResolverExtend.super.isDisplay(entityClass, descriptor, chain);
	}

	@Override
	public io.basc.framework.orm.ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		ForeignKey foreignKey = AnnotatedElementUtils.getMergedAnnotation(descriptor, ForeignKey.class);
		if (foreignKey != null) {
			return new io.basc.framework.orm.ForeignKey(foreignKey.entity(), foreignKey.name());
		}
		return EntityMappingResolverExtend.super.getForeignKey(entityClass, descriptor, chain);
	}

	@Override
	public boolean isConfigurable(TypeDescriptor sourceType, EntityMappingResolver chain) {
		ConfigurationProperties configurationProperties = Annotations.getAnnotation(ConfigurationProperties.class,
				sourceType, sourceType.getType());
		if (configurationProperties != null) {
			return true;
		}
		return EntityMappingResolverExtend.super.isConfigurable(sourceType, chain);
	}

	@Override
	public ObjectMapperContext getContext(TypeDescriptor sourceType, ObjectMapperContext parent,
			EntityMappingResolver chain) {
		ConfigurationProperties configurationProperties = Annotations.getAnnotation(ConfigurationProperties.class,
				sourceType, sourceType.getType());
		if (configurationProperties == null) {
			return EntityMappingResolverExtend.super.getContext(sourceType, parent, chain);
		}

		ObjectMapperContext context = new ObjectMapperContext(parent);
		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}

		if (StringUtils.isNotEmpty(prefix)) {
			PlaceholderFormat placeholderFormat = getPlaceholderFormat();
			if (placeholderFormat != null) {
				prefix = placeholderFormat.replacePlaceholders(prefix);
			}

			prefix = prefix + context.getNameConnector();
		}
		context.setNamePrefix(prefix);
		context.setLoggerLevel(configurationProperties.loggerLevel().getValue());
		return context;
	}

	@Override
	public boolean hasEffectiveValue(Object entity, Parameter parameter, EntityMappingResolver chain) {
		InvalidBaseTypeValue invalidBaseTypeValue = AnnotatedElementUtils.getMergedAnnotation(parameter,
				InvalidBaseTypeValue.class);
		if (invalidBaseTypeValue != null && invalidBaseTypeValue.value().length > 0) {
			Object value = parameter.getSource();
			if (value instanceof Number) {
				double dv = ((Number) value).doubleValue();
				for (double invalid : invalidBaseTypeValue.value()) {
					if (dv == invalid) {
						return false;
					}
				}
			}
		}
		return EntityMappingResolverExtend.super.hasEffectiveValue(entity, parameter, chain);
	}
}
