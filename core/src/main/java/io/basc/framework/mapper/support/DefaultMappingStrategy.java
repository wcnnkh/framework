package io.basc.framework.mapper.support;

import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.IdentityConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.Item;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.check.PredicateRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;
import lombok.Data;

@Data
public class DefaultMappingStrategy implements MappingStrategy {
	private final PredicateRegistry<ParameterDescriptor> predicateRegistry = new PredicateRegistry<>();
	private boolean ignoreNull;
	private ConversionService conversionService = new IdentityConversionService();

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

	private String getName(MappingContext context, Item field) {
		if (context == null) {
			return field.getName();
		}
		return context.getContextFields().reverse().concat(Elements.singleton(field)).map((e) -> e.getName())
				.collect(Collectors.joining("."));
	}

	private Elements<String> getAliasNames(MappingContext context, Item field) {
		if (context == null) {
			return Elements.singleton(field.getName()).concat(field.getAliasNames());
		}

		Elements<Item> fields = context.getContextFields().reverse().concat(Elements.singleton(field));
		// 组合出所有的名称
		List<Elements<String>> recursionNames = CollectionUtils.recursiveComposition(
				fields.map((e) -> Elements.singleton(e.getName()).concat(e.getAliasNames())).toList());
		return Elements.of(
				recursionNames.stream().map((e) -> e.collect(Collectors.joining("."))).collect(Collectors.toList()));
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Item> targetMapping, Item targetField) throws MappingException {
		Elements<String> aliasNames = getAliasNames(targetContext, targetField);
		Elements<Setter> setters = targetField.isSupportSetter() ? aliasNames.map((e) -> targetField.setter().rename(e))
				: Elements.empty();
		for (Setter setter : setters) {
			if (!predicateRegistry.test(setter)) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (Setter setter : setters) {
			if (objectMapper.isEntity(targetType, setter)) {
				Object entity = objectMapper.newInstance(setter.getTypeDescriptor());
				MappingContext entityContext = new MappingContext(targetMapping, targetField, targetContext);
				MappingStrategy strategy = objectMapper.getMappingStrategy(setter.getTypeDescriptor());
				objectMapper.transform(sourceAccess, sourceContext, entity, setter.getTypeDescriptor(), entityContext,
						strategy);
				try {
					setter.set(target, entity);
				} catch (Throwable ex) {
					throw new MappingException(ex);
				}
				return;
			}
		}

		// 名称嵌套处理
		Parameter parameter = null;
		for (String name : aliasNames) {
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
					try {
						setter.set(target, value);
					} catch (Throwable ex) {
						throw new MappingException(ex);
					}
					return;
				}
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Item> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Item> targetMapping,
			Item targetField) throws MappingException {
		Elements<String> aliasNames = getAliasNames(targetContext, targetField);
		Elements<Setter> setters = aliasNames.map((e) -> targetField.setter().rename(e));
		for (Setter setter : setters) {
			if (!predicateRegistry.test(setter)) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (String name : aliasNames) {
			Elements<? extends Item> sourceFields = sourceMapping.getElements(name);
			for (Item sourceField : sourceFields) {
				Getter getter = sourceField.getter().rename(sourceField.getName());
				if (!predicateRegistry.test(getter)) {
					continue;
				}

				// 自动匹配类型
				for (Setter setter : setters) {
					if (conversionService.canConvert(getter.getTypeDescriptor(), setter.getTypeDescriptor())) {
						Object value;
						try {
							value = getter.get(source);
						} catch (Throwable ex) {
							throw new MappingException(ex);
						}
						Object convertedValue = conversionService.convert(value, getter.getTypeDescriptor(),
								setter.getTypeDescriptor());
						try {
							setter.set(target, convertedValue);
						} catch (Throwable ex) {
							throw new MappingException(ex);
						}
						return;
					}
				}
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Item> sourceMapping, Item sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		String name = getName(sourceContext, sourceField);
		Getter getter = sourceField.getter().rename(name);
		if (!predicateRegistry.test(getter)) {
			return;
		}

		Object value;
		try {
			value = getter.get(source);
		} catch (Throwable e) {
			throw new MappingException(e);
		}

		if (isIgnoreNull() && value != null) {
			return;
		}

		Parameter parameter = new Parameter(name, value, getter.getTypeDescriptor());
		targetAccess.set(parameter);
	}

}
