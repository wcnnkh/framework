package io.basc.framework.orm.stereotype;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotationAttributes;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.data.repository.SortOrder;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.config.AnalyzeExtender;
import io.basc.framework.orm.config.Analyzer;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.Symbol;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;

public class AnnotationAnalyzeExtender implements AnalyzeExtender, PlaceholderFormatAware {
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
	public boolean isIgnore(Class<?> entityClass, Analyzer chain) {
		Ignore ignore = entityClass.getAnnotation(Ignore.class);
		if (ignore == null) {
			return chain.isIgnore(entityClass);
		}
		return true;
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
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
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
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
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
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
	public String getName(Class<?> entityClass, Analyzer chain) {
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
	public Elements<String> getAliasNames(Class<?> entityClass, Analyzer chain) {
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
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
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
	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		Nullable nullable = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Nullable.class);
		if (nullable == null) {
			return chain.isNullable(entityClass, descriptor);
		}
		return nullable.value();
	}

	@Override
	public boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor, Analyzer chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Entity.class)) {
			return true;
		}
		return chain.isEntity(source, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source, Analyzer chain) {
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
	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Version.class)) {
			return true;
		}
		return chain.isVersion(entityClass, descriptor);
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			Analyzer chain) {
		NumberRange range = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(),
				NumberRange.class);
		if (range != null) {
			return Elements.singleton(Range.closed(range.min(), range.max()));
		}
		return chain.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), AutoIncrement.class)) {
			return true;
		}

		return chain.isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public String getComment(Class<?> entityClass, Analyzer chain) {
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
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		Comment comment = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Comment.class);
		if (comment != null && StringUtils.hasText(comment.value())) {
			return comment.value();
		}
		return chain.getComment(entityClass, descriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, Analyzer chain) {
		return Annotations.getCharsetName(entityClass, () -> chain.getCharsetName(entityClass));
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return Annotations.getCharsetName(descriptor.getTypeDescriptor(),
				() -> chain.getCharsetName(entityClass, descriptor));
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Unique.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		if (AnnotatedElementUtils.hasAnnotation(descriptor.getTypeDescriptor(), Increment.class)) {
			return true;
		}
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			ColumnDescriptor property, Analyzer chain) {
		SortType sortType = AnnotatedElementUtils.getMergedAnnotation(parameter.getTypeDescriptor(), SortType.class);
		if (sortType != null) {
			SortOrder sortSymbol = SortOrder.forName(sortType.value());
			return new Sort(new Expression(parameter.getName()), sortSymbol);
		}
		return AnalyzeExtender.super.getSort(operationSymbol, repository, parameter, property, chain);
	}

	@Override
	public <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property, Analyzer chain) {
		io.basc.framework.orm.stereotype.Condition condition = AnnotatedElementUtils
				.getMergedAnnotation(parameter.getTypeDescriptor(), io.basc.framework.orm.stereotype.Condition.class);
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
			return new Condition(relationshipSymbol, parameter.getName(), conditionSymbol, parameter.getValue(),
					parameter.getTypeDescriptor());
		}
		return AnalyzeExtender.super.getCondition(operationSymbol, repository, parameter, property, chain);
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		Display display = AnnotatedElementUtils.getMergedAnnotation(entityClass, Display.class);
		if (display != null && StringUtils.equals(display.name(), descriptor.getName())) {
			return true;
		}

		display = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Display.class);
		if (display != null) {
			return true;
		}
		return AnalyzeExtender.super.isDisplay(entityClass, descriptor, chain);
	}

	@Override
	public io.basc.framework.orm.ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor,
			Analyzer chain) {
		ForeignKey foreignKey = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(),
				ForeignKey.class);
		if (foreignKey != null) {
			return new io.basc.framework.orm.ForeignKey(foreignKey.entity(), foreignKey.name());
		}
		return AnalyzeExtender.super.getForeignKey(entityClass, descriptor, chain);
	}

	@Override
	public boolean hasEffectiveValue(Parameter parameter, Analyzer chain) {
		InvalidBaseTypeValue invalidBaseTypeValue = AnnotatedElementUtils
				.getMergedAnnotation(parameter.getTypeDescriptor(), InvalidBaseTypeValue.class);
		if (invalidBaseTypeValue != null && invalidBaseTypeValue.value().length > 0) {
			Object value = parameter.getValue();
			if (value instanceof Number) {
				double dv = ((Number) value).doubleValue();
				for (double invalid : invalidBaseTypeValue.value()) {
					if (dv == invalid) {
						return false;
					}
				}
			}
		}
		return AnalyzeExtender.super.hasEffectiveValue(parameter, chain);
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor, Analyzer chain) {
		Elements<IndexInfo> indexs = chain.getIndexs(sourceClass, descriptor);
		Index index = AnnotatedElementUtils.getMergedAnnotation(descriptor.getTypeDescriptor(), Index.class);
		if (index != null) {
			IndexInfo indexInfo = new IndexInfo(index.name(), index.type(), index.length(), index.method(),
					index.order());
			if (indexs == null) {
				indexs = Elements.singleton(indexInfo);
			} else {
				indexs = indexs.concat(Elements.singleton(indexInfo));
			}
		}
		return indexs;
	}
}
