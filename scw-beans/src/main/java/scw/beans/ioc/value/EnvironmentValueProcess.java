package scw.beans.ioc.value;

import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.ResolvableType;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.Observable;
import scw.mapper.Field;
import scw.util.PropertyPlaceholderHelper;
import scw.value.StringValue;

public class EnvironmentValueProcess extends AbstractValueProcesser {

	@Override
	protected void processInteranl(BeanDefinition beanDefinition, final BeanFactory beanFactory, final Object bean, final Field field, Value value, final String name,
			Charset charset) throws Exception{
		if(field.getSetter().getType() == Observable.class){
			ResolvableType valueType = ResolvableType.forType(field.getSetter().getGenericType());
			valueType = valueType.getGeneric(0);
			Observable<Object> dynamicValue = beanFactory.getEnvironment().getObservableValue(name, valueType.getType(), null);
			field.getSetter().set(bean, dynamicValue);
			//如果是一个动态值就不用进行监听了
			return ;
		}
		
		if(name.startsWith(PropertyPlaceholderHelper.PLACEHOLDER_PREFIX) && name.endsWith(PropertyPlaceholderHelper.PLACEHOLDER_SUFFIX)){
			String v = beanFactory.getEnvironment().resolvePlaceholders(name);
			set(bean, field, name, new StringValue(v));
			return ;
		}
		
		scw.value.Value v = beanFactory.getEnvironment().getValue(name);
		set(bean, field, name, v);
		if (isRegisterListener(beanDefinition, field, value)) {
			beanFactory.getEnvironment().registerListener(name, new EventListener<ChangeEvent<String>>() {

				public void onEvent(ChangeEvent<String> event) {
					try {
						set(bean, field, name, beanFactory.getEnvironment().getValue(name));
					} catch (Exception e) {
						logger.error(e, field);
					}
				}
			});
		}
	}

	protected synchronized void set(final Object bean, final Field field, final String name, scw.value.Value value)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Changes in progress name [{}] field [{}] value [{}]", name, field.getSetter(), value);
		}
		field.getSetter().set(bean, value == null ? null : value.getAsObject(field.getSetter().getGenericType()));
	}
}
