package io.basc.framework.orm.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotationAttributes;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.SortSymbol;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.filter.FilterableMappingStrategy;
import io.basc.framework.mapper.filter.ParameterDescriptorFilter;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.support.EntityMappingResolverExtend;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.Symbol;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;

public class AnnotationObjectRelationalResolverExtend implements EntityMappingResolverExtend, PlaceholderFormatAware {
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
		String name = getAnnotationFeldName(descriptor.getTypeDescriptor());
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
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		Elements<String> elements = chain.getAliasNames(entityClass, descriptor);
		Set<String> names = new LinkedHashSet<String>();
		String name = getAnnotationFeldName(descriptor.getTypeDescriptor());
		if (name != null) {
			names.add(name);
		}

		String[] aliasArray = getAnnotatedAlias(descriptor.getTypeDescriptor());
		if (aliasArray != null) {
			for (String alias : aliasArray) {
				if (StringUtils.isNotEmpty(alias)) {
					names.add(alias);
				}
			}
		}
		return elements.concat(Elements.forArray(names.toArray(new String[0])));
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
	public Elements<String> getAliasNames(Class<?> entityClass, EntityMappingResolver chain) {
		Elements<String> elements = chain.getAliasNames(entityClass);
		Set<String> list = new LinkedHashSet<String>(8);
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
		return elements.concat(Elements.forArray((list.toArray(new String[0]))));
	}

	@Override
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		// TODO 为什么使用AnnotatedElementUtils.hasAnnotation无法获取到
		if (descriptor.getTypeDescriptor().isAnnotationPresent(PrimaryKey.class)) {
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
		Nullable nullable = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Nullable.class);
		if (nullable == null) {
			return chain.isNullable(entityClass, descriptor);
		}
		return nullable.value();
	}

	@Override
	public boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Entity.class)) {
			return true;
		}
		return chain.isEntity(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(source, Entity.class)) {
			return true;
		}
		Class<?> clazz = source.getType();
		while (clazz != null && clazz != Object.class) {
			if (AnnotatedElementUtils.hasAnnotation(clazz, Entity.class)) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return chain.isEntity(source);
	}

	@Override
	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Version.class)) {
			return true;
		}
		return chain.isVersion(entityClass, descriptor);
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		NumberRange range = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(),
				NumberRange.class);
		if (range != null) {
			return Elements.singleton(Range.closed(range.min(), range.max()));
		}
		return chain.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), AutoIncrement.class)) {
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
		Comment comment = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Comment.class);
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
		return Annotations.getCharsetName(descriptor.getTypeDescriptor(),
				() -> chain.getCharsetName(entityClass, descriptor));
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Unique.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Increment.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public Elements<? extends Sort> getSorts(OperationSymbol operationSymbol, TypeDescriptor source,
			ParameterDescriptor descriptor, EntityMappingResolver chain) {
		SortType sortType = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), SortType.class);
		if (sortType != null) {
			SortSymbol sortSymbol = Symbol.getOrCreate(() -> SortSymbol.getSortSymbols(sortType.value()).first(),
					() -> new SortSymbol(sortType.value()));
			;
			return Elements.singleton(new Sort(descriptor.getName(), sortSymbol));
		}
		return EntityMappingResolverExtend.super.getSorts(operationSymbol, source, descriptor, chain);
	}

	@Override
	public Elements<? extends io.basc.framework.data.repository.Condition> getConditions(
			OperationSymbol operationSymbol, TypeDescriptor source, Parameter parameter, EntityMappingResolver chain) {
		io.basc.framework.orm.annotation.Condition condition = AnnotatedElementUtils
				.getMergedAnnotation(parameter.getTypeDescriptor(), io.basc.framework.orm.annotation.Condition.class);
		if (condition != null) {
			ConditionSymbol conditionSymbol = Symbol.getOrCreate(
					() -> ConditionSymbol.getConditionSymbols(condition.value()).first(),
					() -> new ConditionSymbol(condition.value()));

			Relationship relationship = AnnotatedElementUtils.getMergedAnnotation(parameter.getTypeDescriptor(),
					Relationship.class);
			RelationshipSymbol relationshipSymbol = RelationshipSymbol.AND;
			if (relationship != null) {
				relationshipSymbol = RelationshipSymbol.getOrCreate(
						() -> RelationshipSymbol.getRelationshipSymbol(relationship.value()).first(),
						() -> new RelationshipSymbol(relationship.value()));
			}
			return Elements.singleton(new Condition(relationshipSymbol, new Expression(parameter), conditionSymbol,
					new Expression(parameter)));
		}
		return EntityMappingResolverExtend.super.getConditions(operationSymbol, source, parameter, chain);
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		Display display = AnnotatedElementUtils.getMergedAnnotation(entityClass, Display.class);
		if (display != null && StringUtils.equals(display.name(), descriptor.getName())) {
			return true;
		}

		display = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Display.class);
		if (display != null) {
			return true;
		}
		return EntityMappingResolverExtend.super.isDisplay(entityClass, descriptor, chain);
	}

	@Override
	public io.basc.framework.orm.ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		ForeignKey foreignKey = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(),
				ForeignKey.class);
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
	public MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy,
			EntityMappingResolver chain) {
		MappingStrategy mappingStrategy = EntityMappingResolverExtend.super.getMappingStrategy(source,
				dottomlessMappingStrategy, chain);
		ConfigurationProperties configurationProperties = Annotations.getAnnotation(ConfigurationProperties.class,
				source, source.getType());
		if (configurationProperties == null) {
			return mappingStrategy;
		}

		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}

		if (StringUtils.isNotEmpty(prefix)) {
			PlaceholderFormat placeholderFormat = getPlaceholderFormat();
			if (placeholderFormat != null) {
				prefix = placeholderFormat.replacePlaceholders(prefix);
			}
		}

		// TODO 后续优化Naming时一起优化
		final String namePrefix = prefix + ".";
		ParameterDescriptorFilter filter = new ParameterDescriptorFilter();
		filter.getPredicateRegistry().and((parameter) -> {
			if (parameter.getName().startsWith(namePrefix)) {
				return true;
			}
			return false;
		});

		return new FilterableMappingStrategy(Arrays.asList(filter), mappingStrategy);
	}

	@Override
	public boolean hasEffectiveValue(TypeDescriptor source, Parameter parameter, EntityMappingResolver chain) {
		InvalidBaseTypeValue invalidBaseTypeValue = AnnotatedElementUtils
				.getMergedAnnotation(parameter.getTypeDescriptor(), InvalidBaseTypeValue.class);
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
		return EntityMappingResolverExtend.super.hasEffectiveValue(source, parameter, chain);
	}
}
