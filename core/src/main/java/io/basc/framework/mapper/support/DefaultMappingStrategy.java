package io.basc.framework.mapper.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
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
import io.basc.framework.mapper.name.Naming;
import io.basc.framework.util.Elements;
import io.basc.framework.util.PredicateRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;

public class DefaultMappingStrategy implements MappingStrategy {
	private final PredicateRegistry<ParameterDescriptor> predicateRegistry = new PredicateRegistry<>();
	private boolean ignoreNull;
	private ConversionService conversionService;
	private Naming naming;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.getEnv().getConversionService() : conversionService;
	}

	public final PredicateRegistry<ParameterDescriptor> getPredicateRegistry() {
		return predicateRegistry;
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setConversionService(ConversionService conversionService) {
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

	private void appendMapProperty(Map<String, Object> valueMap, String name, ObjectAccess sourceAccess,
			Naming naming) {
		for (String key : sourceAccess.keys()) {
			if (StringUtils.isNotEmpty(name) && (key.equals(name) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(name)) {
				Parameter parameter = sourceAccess.get(key);
				if (parameter == null) {
					continue;
				}

				if (isIgnoreNull() && !parameter.isPresent()) {
					continue;
				}

				if (!predicateRegistry.test(parameter)) {
					// 不通过
					continue;
				}

				String entityKey = StringUtils.isEmpty(name) ? key
						: key.substring(
								StringUtils.isEmpty(naming.getDelimiter()) ? 0 : naming.getDelimiter().length());
				parameter.setConverterIfAbsent(getConversionService());
				valueMap.put(entityKey, parameter);
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, ObjectAccess sourceAccess, MappingContext sourceContext,
			Object target, TypeDescriptor targetType, MappingContext targetContext,
			Mapping<? extends Field> targetMapping, Field targetField) throws MappingException {
		Elements<Field> fields = targetContext.parents().map((e) -> e.getContext()).filter((e) -> e != null)
				.concat(Elements.singleton(targetField));

		Elements<Elements<String>> parentAliasNames = targetContext.parents()
				.map((e) -> e.getContext().getAliasNames());
		// 组合出各种别名

		for (Elements<String> alisNames : parentAliasNames) {
			List<String> list = new ArrayList<>();
			Iterator<String> iterator = alisNames.iterator();
			while (iterator.hasNext()) {

			}
			for (String name : alisNames) {
				list.add(name);
			}
		}

		Elements<Setter> setters = targetField.getSetters().map((e) -> e.rename(targetField.getName())).toList();
		for (Setter setter : targetField.getSetters()) {
			if (!predicateRegistry.test(setter.rename(targetField.getName()))) {
				// 只要有一个校验不通过就直接return
				return;
			}
		}

		for (Setter setter : targetField.getSetters()) {
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
		for (String name : targetField.getAliasNames()) {
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
				parameter = null;
				continue;
			}
		}

		if (parameter == null) {
			for (Setter setter : targetField.getSetters()) {
				if (!predicateRegistry.test(setter)) {
					continue;
				}

				if (setter.getTypeDescriptor().isMap()) {
					Map<String, Object> valueMap = new LinkedHashMap<>();
					for (String name : targetField.getAliasNames()) {
						appendMapProperty(valueMap, name, sourceAccess, naming);
					}

					Object value = getConversionService().convert(valueMap,
							TypeDescriptor.map(LinkedHashMap.class, String.class, Value.class),
							setter.getTypeDescriptor());
					setter.set(target, value);
					return;
				}
			}
		} else {
			for (Setter setter : targetField.getSetters()) {
				if (!predicateRegistry.test(parameter)) {
					continue;
				}

				if (setter.test(parameter)) {
					setter.set(target, parameter.getSource());
					return;
				}
			}

			Setter setter = targetField.getSetters().filter(predicateRegistry).first();
			if (setter == null) {
				return;
			}

			setter.set(target, parameter.getAsObject(setter.getTypeDescriptor()));
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends Field> targetMapping,
			Field targetField) throws MappingException {
		for (String name : targetField.getAliasNames()) {
			Elements<? extends Field> sourceFields = sourceMapping.getElements(name);
			for (Field sourceField : sourceFields) {
				// 自动匹配类型
				for (Getter getter : sourceField.getGetters()) {
					if (!predicateRegistry.test(getter.rename(sourceField.getName()))) {
						continue;
					}

					for (Setter setter : targetField.getSetters()) {
						if (!predicateRegistry.test(setter.rename(name))) {
							continue;
						}

						if (setter.test(getter)) {
							Object value = getter.get(source);
							setter.set(target, value);
							return;
						}
					}
				}
			}
		}

		// 没有找到匹配的类型
		Setter setter = targetField.getSetters().filter(predicateRegistry).first();
		if (setter == null) {
			return;
		}

		for (String name : targetField.getAliasNames()) {
			Elements<? extends Field> sourceFields = sourceMapping.getElements(name);
			for (Field sourceField : sourceFields) {
				Getter getter = sourceField.getGetters().filter(predicateRegistry).first();
				if (getter == null) {
					continue;
				}

				Object value = getter.get(source);
				if (isIgnoreNull() && value != null) {
					continue;
				}

				setter.set(target, value);
				return;
			}
		}
	}

	@Override
	public void transform(ObjectMapper objectMapper, Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends Field> sourceMapping, Field sourceField,
			ObjectAccess targetAccess, MappingContext targetContext) throws MappingException {
		for (Getter getter : sourceField.getGetters()) {
			// TODO 错了，应该用field过滤
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
