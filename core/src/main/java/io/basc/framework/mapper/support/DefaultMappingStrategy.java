package io.basc.framework.mapper.support;

import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.IdentityConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.access.ObjectAccess;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.MappingContext;
import io.basc.framework.mapper.entity.MappingException;
import io.basc.framework.mapper.entity.MappingStrategy;
import io.basc.framework.mapper.filter.ConfigurableParameterDescriptorMatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultMappingStrategy extends ConfigurableParameterDescriptorMatcher implements MappingStrategy {
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

		if (!test(sourceAccess.getTypeDescriptor(), parameter)) {
			// 不通过
			return;
		}

		targetAccess.set(parameter);
	}

	private String getName(MappingContext context, FieldDescriptor field) {
		if (context == null) {
			return field.getName();
		}
		return context.getContextFields().reverse().concat(Elements.singleton(field)).map((e) -> e.getName())
				.collect(Collectors.joining("."));
	}

	private Elements<String> getAliasNames(MappingContext context, FieldDescriptor field) {
		if (context == null) {
			return Elements.singleton(field.getName()).concat(field.getAliasNames());
		}

		Elements<FieldDescriptor> fields = context.getContextFields().reverse().concat(Elements.singleton(field));
		// 组合出所有的名称
		List<Elements<String>> recursionNames = CollectionUtils.recursiveComposition(
				fields.map((e) -> Elements.singleton(e.getName()).concat(e.getAliasNames())).toList());
		return Elements.of(
				recursionNames.stream().map((e) -> e.collect(Collectors.joining("."))).collect(Collectors.toList()));
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends FieldDescriptor> targetMapping, FieldDescriptor targetField) throws MappingException {
		Elements<String> aliasNames = getAliasNames(targetContext, targetField);
		Elements<Setter> setters = targetField.isSupportSetter() ? aliasNames.map((e) -> targetField.setter().rename(e))
				: Elements.empty();
		for (Setter setter : setters) {
			if (!test(targetType, setter)) {
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
				setter.set(target, entity);
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

			if (!test(sourceAccess.getTypeDescriptor(), parameter)) {
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
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends FieldDescriptor> targetMapping,
			FieldDescriptor targetField) throws MappingException {
		Elements<String> aliasNames = getAliasNames(targetContext, targetField);
		Elements<Setter> setters = aliasNames.map((e) -> targetField.setter().rename(e));
		for (Setter setter : setters) {
			if (!test(targetType, setter)) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (String name : aliasNames) {
			Elements<? extends FieldDescriptor> sourceFields = sourceMapping.getElements(name);
			for (FieldDescriptor sourceField : sourceFields) {
				Getter getter = sourceField.getter().rename(sourceField.getName());
				if (!test(sourceType, getter)) {
					continue;
				}

				// 自动匹配类型
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

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends FieldDescriptor> sourceMapping, FieldDescriptor sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		String name = getName(sourceContext, sourceField);
		Getter getter = sourceField.getter().rename(name);
		if (!test(sourceType, getter)) {
			return;
		}

		Object value = getter.get(source);

		if (isIgnoreNull() && value != null) {
			return;
		}

		Parameter parameter = new Parameter(name, value, getter.getTypeDescriptor());
		targetAccess.set(parameter);
	}

}
