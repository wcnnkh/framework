package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderReplacer;

public class EnvironmentValueProceor extends AbstractValueProcessor {

	public static boolean isEnvironment(String name) {
		return name.startsWith(PlaceholderReplacer.PLACEHOLDER_PREFIX)
				&& name.endsWith(PlaceholderReplacer.PLACEHOLDER_SUFFIX);
	}

	@Override
	protected void processInteranl(BeanDefinition beanDefinition, final Context context, final Object bean,
			final Field field, ValueDefinition valueDefinition, Charset charset) throws Exception {
		if (field.getSetter().getType() == Observable.class) {
			Observable<Object> dynamicValue = null;
			for (String name : valueDefinition.getNames()) {
				if (valueDefinition.isRequired() && !context.getProperties().containsKey(name)) {
					continue;
				}

				dynamicValue = context.getProperties().getObservable(name).map((e) -> e.getAsObject(
						ResolvableType.forType(field.getSetter().getGenericType()).getGeneric(0).getType()));
				if (dynamicValue != null) {
					break;
				}
			}

			if (dynamicValue == null) {
				return;
			}

			field.getSetter().set(bean, dynamicValue);
			// 如果是一个动态值就不用进行监听了
			return;
		}

		for (String name : valueDefinition.getNames()) {
			if (isEnvironment(name)) {
				String v = context.getProperties().replacePlaceholders(name);
				set(context.getConversionService(), bean, field, name, v);
				return;
			}

			if (valueDefinition.isRequired() && !context.getProperties().containsKey(name)) {
				continue;
			}

			io.basc.framework.value.Value v = context.getProperties().get(name);
			set(context.getConversionService(), bean, field, name, v);
			if (isRegisterListener(beanDefinition, field, valueDefinition)) {
				context.getProperties().getKeyEventRegistry().registerListener((event) -> {
					if (!event.getSource().anyMatch((e) -> StringUtils.equals(e, name))) {
						return;
					}
					try {
						set(context.getConversionService(), bean, field, name, context.getProperties().get(name));
					} catch (Exception e) {
						logger.error(e, field.toString());
					}
				});
			}
		}
	}

	protected void set(ConversionService conversionService, final Object bean, final Field field, final String name,
			Object value) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Changes in progress name [{}] field [{}] value [{}]", name, field.getSetter(), value);
		}
		MapperUtils.setValue(conversionService, bean, field, value);
	}
}
