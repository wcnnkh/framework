package scw.beans.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.beans.BeanDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.mapper.MapperUtils;

public abstract class AbstractIocProcessor implements IocProcessor {
	protected static Logger logger = LoggerUtils.getLogger(IocProcessor.class);

	protected boolean acceptModifiers(BeanDefinition beanDefinition, Object bean, int modifiers){
		if(bean == null){
			return Modifier.isStatic(modifiers);
		}
		return true;
	}
	
	protected void checkMethod(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			logger.warn("class [{}] method [{}] is a static", method.getDeclaringClass(), method);
		}
	}
	
	public void checkField(Object obj, Field field) {
		if (Modifier.isStatic(field.getSetter().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static", field
					.getSetter().getDeclaringClass(), field.getSetter()
					.getName());
		}
		
		if (MapperUtils.isExistValue(field, obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					field.getSetter().getDeclaringClass().getName(),
					field.getSetter().getName());
		}
	}
}
