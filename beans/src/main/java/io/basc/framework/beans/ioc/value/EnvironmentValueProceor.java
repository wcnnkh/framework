package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.Observable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.placeholder.PlaceholderReplacer;

import java.nio.charset.Charset;

public class EnvironmentValueProceor extends AbstractValueProcessor {
	
	public static boolean isEnvironment(String name) {
		return name.startsWith(PlaceholderReplacer.PLACEHOLDER_PREFIX) && name.endsWith(PlaceholderReplacer.PLACEHOLDER_SUFFIX);
	}

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
		
		if(isEnvironment(name)){
			String v = beanFactory.getEnvironment().resolvePlaceholders(name);
			set(beanFactory.getEnvironment().getConversionService(), bean, field, name, v);
			return ;
		}
		
		io.basc.framework.value.Value v = beanFactory.getEnvironment().getValue(name);
		set(beanFactory.getEnvironment().getConversionService(), bean, field, name, v);
		if (isRegisterListener(beanDefinition, field, value)) {
			beanFactory.getEnvironment().registerListener(name, new EventListener<ChangeEvent<String>>() {

				public void onEvent(ChangeEvent<String> event) {
					try {
						set(beanFactory.getEnvironment().getConversionService(), bean, field, name, beanFactory.getEnvironment().getValue(name));
					} catch (Exception e) {
						logger.error(e, field.toString());
					}
				}
			});
		}
	}

	protected synchronized void set(ConversionService conversionService, final Object bean, final Field field, final String name, Object value)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Changes in progress name [{}] field [{}] value [{}]", name, field.getSetter(), value);
		}
		MapperUtils.setValue(conversionService, bean, field, value);
	}
}
