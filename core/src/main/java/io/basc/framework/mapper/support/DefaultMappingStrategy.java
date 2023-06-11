package io.basc.framework.mapper.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.IdentityConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.PredicateRegistry;

public class DefaultMappingStrategy implements MappingStrategy {
	private final PredicateRegistry<ParameterDescriptor> predicateRegistry = new PredicateRegistry<>();
	private boolean ignoreNull;
	private ConversionService conversionService = new IdentityConversionService();

	public ConversionService getConversionService() {
		return conversionService;
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setConversionService(ConversionService conversionService) {
		Assert.requiredArgument(conversionService != null, "conversionService");
		this.conversionService = conversionService;
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			String name, ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		Parameter parameter = sourceAccess.get(name);
		if (parameter == null) {
			return;
		}

		if (isIgnoreNull() && !parameter.isPresent()) {
			// 如果忽略空，但目标为空就忽略
			return;
		}

		if (!predicateRegistry.test(parameter)) {
			// 不通过
			return;
		}

		targetAccess.set(parameter);
	}

	private List<List<String>> recursion(Elements<Elements<String>> elements) {
		List<List<String>> targetList = new ArrayList<>();
		Iterator<Elements<String>> iterator = elements.iterator();
		while (iterator.hasNext()) {
			recursion(iterator, iterator.next().iterator(), new ArrayList<>(), targetList);
		}
		return targetList;
	}

	private void recursion(Iterator<Elements<String>> iterator, Iterator<String> nameIterator, List<String> rootNames,
			List<List<String>> targetList) {
		if (nameIterator.hasNext()) {
			String name = nameIterator.next();
			List<String> list = new ArrayList<>(rootNames);
			list.add(name);
			if (iterator.hasNext()) {
				recursion(iterator, nameIterator, list, targetList);
			} else {
				// 到底了
				targetList.add(list);
			}
		}

		if (iterator.hasNext()) {
			recursion(iterator, iterator.next().iterator(), rootNames, targetList);
		}
	}

	private Elements<String> getSetterNames(MappingContext targetContext, Field targetField) {
		if (targetContext == null) {
			return Elements.singleton(targetField.getName()).concat(targetField.getAliasNames());
		}

		Elements<Field> fields = targetContext.parents().reverse().map((e) -> e.getField()).filter((e) -> e != null)
				.concat(Elements.singleton(targetField));
		// 组合出所有的名称
		List<List<String>> recursionNames = recursion(
				fields.map((e) -> Elements.singleton(e.getName()).concat(e.getAliasNames())));
		return Elements.of(recursionNames.stream().map((e) -> e.stream().collect(Collectors.joining(".")))
				.collect(Collectors.toList()));
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField) throws MappingException {
		Elements<String> setterNames = getSetterNames(targetContext, targetField);
		Elements<Setter> setters = targetField.getSetters().flatMap((e) -> setterNames.map((name) -> e.rename(name)));
		for (Setter setter : setters) {
			if (!predicateRegistry.test(setter)) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (Setter setter : setters) {
			if (objectMapper.isEntity(setter.getTypeDescriptor())) {
				Object entity = objectMapper.newInstance(setter.getTypeDescriptor());
				MappingContext entityContext = new MappingContext(targetMapping, targetField, targetContext);
				MappingStrategy strategy = objectMapper.getMappingStrategy(setter.getTypeDescriptor());
				objectMapper.transform(sourceAccess, sourceContext, entity, setter.getTypeDescriptor(), entityContext,
						strategy);
				setter.set(target, entity);
				return;
			}
		}

		// 名称嵌套处理
		Parameter parameter = null;
		for (String name : setterNames) {
			parameter = sourceAccess.get(name);
			if (parameter == null) {
				continue;
			}

			if (isIgnoreNull() && !parameter.isPresent()) {
				// 如果忽略空，但目标为空就忽略
				parameter = null;
				continue;
			}

			if (!predicateRegistry.test(parameter)) {
				// 有一个验证不通过就忽略全部
				parameter = null;
				// TODO continue还是return
				continue;
			}

			for (Setter setter : setters) {
				if (conversionService.canConvert(parameter.getTypeDescriptor(), setter.getTypeDescriptor())) {
					Object value = conversionService.convert(parameter.getSource(), parameter.getTypeDescriptor(),
							setter.getTypeDescriptor());
					setter.set(target, value);
					return;
				}
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Field> targetMapping,
			Field targetField) throws MappingException {
		Elements<String> setterNames = getSetterNames(targetContext, targetField);
		Elements<Setter> setters = targetField.getSetters().flatMap((e) -> setterNames.map((name) -> e.rename(name)));
		for (Setter setter : setters) {
			if (!predicateRegistry.test(setter)) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (String name : setterNames) {
			Elements<? extends Field> sourceFields = sourceMapping.getElements(name);
			for (Field sourceField : sourceFields) {
				Elements<Getter> getters = sourceField.getGetters().map((e) -> e.rename(e.getName()));
				if (!getters.anyMatch(predicateRegistry)) {
					// 有一个不允许就忽略
					continue;
				}

				// 自动匹配类型
				for (Getter getter : getters) {
					for (Setter setter : setters) {
						if (conversionService.canConvert(getter.getTypeDescriptor(), setter.getTypeDescriptor())) {
							Object value = getter.get(source);
							Object convertedValue = conversionService.convert(value, getter.getTypeDescriptor(),
									setter.getTypeDescriptor());
							setter.set(target, convertedValue);
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Field sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		for (Getter getter : sourceField.getGetters()) {
			if (!predicateRegistry.test(getter)) {
				continue;
			}

			Object value = getter.get(source);
			if (isIgnoreNull() && value != null) {
				continue;
			}

			Parameter parameter = new Parameter(sourceField.getName(), value, getter.getTypeDescriptor());
			targetAccess.set(parameter);
		}
	}

}
