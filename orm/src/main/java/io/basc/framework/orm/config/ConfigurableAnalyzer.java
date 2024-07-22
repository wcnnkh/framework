package io.basc.framework.orm.config;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.MergedElements;

public class ConfigurableAnalyzer extends ConfigurableServices<AnalyzeExtender> implements Analyzer {
	/**
	 * 是否将驼峰命名转换为下划线的名称 myAbc-> my_abc
	 */
	private boolean humpNamingReplacement = false;

	public ConfigurableAnalyzer() {
		super(AnalyzeExtender.class);
	}

	private void appendDefaultAliasNames(Collection<String> names, String name) {
		String humpName = StringUtils.humpNamingReplacement(name, "_");
		if (!humpName.equals(name)) {
			names.add(humpName);
		}

		String humpName2 = StringUtils.humpNamingReplacement(name, "-");
		if (!humpName2.equals(name)) {
			names.add(humpName2);
		}
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass) {
		Elements<String> parentNames = ChainAnalyzer.build(getServices().iterator()).getAliasNames(entityClass);
		Set<String> names = new LinkedHashSet<>(8);
		// 如果没有使用过别名，那就设置默认名称
		String defaultName = getName(entityClass);
		names.add(defaultName);
		appendDefaultAliasNames(names, defaultName);
		return new MergedElements<>(parentNames, Elements.of(names));
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		Elements<String> parentNames = ChainAnalyzer.build(getServices().iterator()).getAliasNames(entityClass,
				descriptor);
		Set<String> names = new LinkedHashSet<>(8);
		String defaultName = getName(entityClass, descriptor);
		names.add(defaultName);
		appendDefaultAliasNames(names, defaultName);

		if (isEntity(TypeDescriptor.valueOf(entityClass), descriptor)) {
			getAliasNames(descriptor.getTypeDescriptor().getType()).forEach(names::add);
		}
		return new MergedElements<>(parentNames, Elements.of(names));
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		return ChainAnalyzer.build(getServices().iterator()).getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).getCharsetName(entityClass, descriptor);
	}

	@Override
	public <T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, T entity) {
		String name = ChainAnalyzer.build(getServices().iterator()).getRepositoryName(operationSymbol, entityMapping,
				entityClass, entity);
		return StringUtils.isEmpty(name) ? entityMapping.getName() : name;
	}

	@Override
	public <T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		Expression expression = ChainAnalyzer.build(getServices().iterator()).getColumn(operationSymbol, repository,
				parameter, property);
		if (expression == null) {
			expression = new Expression(parameter.getName(), parameter.getValue(), parameter.getTypeDescriptor());
		}
		return expression;
	}

	@Override
	public <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		Condition condition = ChainAnalyzer.build(getServices().iterator()).getCondition(operationSymbol, repository,
				parameter, property);
		if (condition == null) {
			condition = new Condition(parameter.getName(), ConditionSymbol.EQU, parameter.getValue(),
					parameter.getTypeDescriptor());
		}
		return condition;
	}

	@Override
	public <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			ColumnDescriptor property) {
		return ChainAnalyzer.build(getServices().iterator()).getSort(operationSymbol, repository, parameter, property);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		return ChainAnalyzer.build(getServices().iterator()).getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).getComment(entityClass, descriptor);
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = entityClass.getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	private String getDefaultName(ParameterDescriptor descriptor) {
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(descriptor.getName(), "_")
				: descriptor.getName();
	}

	@Override
	public ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).getForeignKey(entityClass, descriptor);
	}

	@Override
	public String getName(Class<?> entityClass) {
		String name = ChainAnalyzer.build(getServices().iterator()).getName(entityClass);
		if (StringUtils.isEmpty(name)) {
			name = getDefaultEntityName(entityClass);
		}
		return name;
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		String name = ChainAnalyzer.build(getServices().iterator()).getName(entityClass, descriptor);
		return StringUtils.isEmpty(name) ? getDefaultName(descriptor) : name;
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).getNumberRanges(entityClass, descriptor);
	}

	@Override
	public boolean hasEffectiveValue(Parameter parameter) {
		return ChainAnalyzer.build(getServices().iterator()).hasEffectiveValue(parameter);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isDisplay(entityClass, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor) {
		return isEntity(descriptor.getTypeDescriptor())
				|| ChainAnalyzer.build(getServices().iterator()).isEntity(source, descriptor);
	}

	@Override
	public boolean isEntity(TypeDescriptor source) {
		return ChainAnalyzer.build(getServices().iterator()).isEntity(source);
	}

	public boolean isHumpNamingReplacement() {
		return humpNamingReplacement;
	}

	@Override
	public boolean isIgnore(Class<?> entityClass) {
		return ChainAnalyzer.build(getServices().iterator()).isIgnore(entityClass);
	}

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isIgnore(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isIncrement(entityClass, descriptor);
	}

	@Override
	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor) {
		return !isPrimaryKey(entityClass, descriptor)
				&& ChainAnalyzer.build(getServices().iterator()).isNullable(entityClass, descriptor);
	}

	@Override
	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isPrimaryKey(entityClass, descriptor);
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).isVersion(entityClass, descriptor);
	}

	public void setHumpNamingReplacement(boolean humpNamingReplacement) {
		this.humpNamingReplacement = humpNamingReplacement;
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor) {
		return ChainAnalyzer.build(getServices().iterator()).getIndexs(sourceClass, descriptor);
	}

	@Override
	public PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor source,
			PropertiesTransformStrategy dottomlessStrategy) {
		return ChainAnalyzer.build(getServices().iterator()).getPropertiesTransformStrategy(source, dottomlessStrategy);
	}
}
