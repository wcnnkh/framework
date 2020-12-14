package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.event.EventListener;
import scw.event.Observable;
import scw.event.ObservableEvent;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractObservableValueProcesser<R> extends AbstractValueProcesser {

	@Override
	protected void processInteranl(final BeanDefinition beanDefinition, final BeanFactory beanFactory,
			final PropertyFactory propertyFactory, final Object bean, final Field field, final Value value,
			final String name, final String charsetName) throws Exception {
		Observable<R> res = getObservableResource(beanDefinition, beanFactory, propertyFactory, bean, field,
				value, name, charsetName);
		set(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName, res.get(),
				false);

		if (isRegisterListener(beanDefinition, field, value)) {
			res.registerListener(new EventListener<ObservableEvent<R>>() {

				public void onEvent(ObservableEvent<R> event) {
					try {
						set(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName,
								event.getSource(), true);
					} catch (Exception e) {
						logger.error(e, field);
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
	 * @param insertNull
	 *            是否可以插入空值
	 */
	protected synchronized void set(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, R res, boolean insertNull)
			throws Exception {
		Object v = null;
		if (res == null) {
			logger.warn("nonexistent resources name [{}] field [{}]", name, field.getSetter());
		} else {
			v = parse(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName, res);
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

	protected abstract Observable<R> getObservableResource(BeanDefinition beanDefinition,
			BeanFactory beanFactory, PropertyFactory propertyFactory, Object bean, Field field, Value value,
			String name, String charsetName);

	protected abstract Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Object bean, Field field, Value value, String name, String charsetName,
			R resource) throws Exception;
}
