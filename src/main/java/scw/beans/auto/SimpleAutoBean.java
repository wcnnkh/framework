package scw.beans.auto;

import java.lang.reflect.Constructor;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class SimpleAutoBean extends AbstractAutoBean {
	private static Logger logger = LoggerUtils.getLogger(SimpleAutoBean.class);
	private PropertyFactory propertyFactory;
	private Constructor<?> autoConstructor;

	public SimpleAutoBean(BeanFactory beanFactory, Class<?> type, PropertyFactory propertyFactory) {
		super(beanFactory, type);
		this.propertyFactory = propertyFactory;
		this.autoConstructor = AutoBeanUtils.getAutoConstructor(type, beanFactory, propertyFactory);
		if (autoConstructor != null) {
			//默认的构造方法不显示日志
			if(autoConstructor.getParameterTypes().length != 0){
				logger.info(autoConstructor);
			}else{
				if(logger.isDebugEnabled()){
					logger.debug(autoConstructor);
				}
			}
		}
	}

	public boolean isInstance() {
		return autoConstructor != null;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return autoConstructor == null ? null : autoConstructor.getParameterTypes();
	}

	@Override
	protected Object[] getParameters() {
		return autoConstructor == null ? null
				: AutoBeanUtils.getAutoArgs(type, autoConstructor, beanFactory, propertyFactory);
	}

}
