package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.mapper.Field;

public abstract class AbstractObservableValueProcessor<R> extends AbstractValueProcessor {

	@Override
	protected void processInteranl(final BeanDefinition beanDefinition, final Context context, final Object bean,
			final Field field, final ValueDefinition valueDefinition, final Charset charset) throws Exception {
		Observable<R> res = null;
		String nameToUse = null;
		for (String name : valueDefinition.getNames()) {
			nameToUse = name;
			res = getObservableResource(beanDefinition, context, bean, field, valueDefinition, name, charset);
			if (res != null) {
				break;
			}
		}

		if (res == null) {
			if (valueDefinition.isRequired()) {
				throw new NotSupportedException(field.getSetter().toString());
			}
			return;
		}

		set(beanDefinition, context, bean, field, valueDefinition, nameToUse, charset, res.get(), false);

		if (isRegisterListener(beanDefinition, field, valueDefinition)) {
			final String name = nameToUse;
			res.registerListener(new EventListener<ChangeEvent<R>>() {

				public void onEvent(ChangeEvent<R> event) {
					try {
						set(beanDefinition, context, bean, field, valueDefinition, name, charset, event.getSource(),
								true);
					} catch (Exception e) {
						logger.error(e, field.toString());
					}
				}
			});
		}
	}

	/**
	 * @param beanDefinition
	 * @param beanFactory
	 * @param propertyFactory
	 * @param bean
	 * @param field
	 * @param value
	 * @param name
	 * @param charsetName
	 * @param res
	 * @param insertNull      是否可以插入空值
	 */
	protected synchronized void set(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition valueDefinition, String name, Charset charset, R res, boolean insertNull) throws Exception {
		Object v = null;
		if (res == null) {
			logger.warn("nonexistent resources name [{}] field [{}]", name, field.getSetter());
		} else {
			v = parse(beanDefinition, context, bean, field, valueDefinition, name, charset, res);
			if (v == null) {
				logger.warn("value is a null name [{}] field [{}]", name, field.getSetter());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Changes in progress name [{}] field [{}] value {}", name, field.getSetter(), v);
		}

		if (v == null && !insertNull) {
			logger.warn("Cannot insert null value name [{}] field [{}]", name, field.getSetter());
			return;
		}
		field.getSetter().set(bean, v);
	}

	protected abstract Observable<R> getObservableResource(BeanDefinition beanDefinition, Context context, Object bean,
			Field field, ValueDefinition valueDefinition, String name, Charset charset);

	protected abstract Object parse(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition valueDefinition, String name, Charset charset, R resource) throws Exception;
}
