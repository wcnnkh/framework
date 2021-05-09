package scw.beans.ioc.value;

import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.convert.ConversionService;
import scw.core.ResolvableType;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.Observable;
import scw.mapper.Field;
import scw.mapper.MapperUtils;
import scw.util.placeholder.PlaceholderReplacer;

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
		
		if(name.startsWith(PlaceholderReplacer.PLACEHOLDER_PREFIX) && name.endsWith(PlaceholderReplacer.PLACEHOLDER_SUFFIX)){
			String v = beanFactory.getEnvironment().resolvePlaceholders(name);
			set(beanFactory.getEnvironment().getConversionService(), bean, field, name, v);
			return ;
		}
		
		scw.value.Value v = beanFactory.getEnvironment().getValue(name);
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
