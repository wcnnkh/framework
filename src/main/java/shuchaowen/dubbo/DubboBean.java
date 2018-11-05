package shuchaowen.dubbo;

import shuchaowen.core.beans.AnnotationBean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.exception.NotSupportException;

import com.alibaba.dubbo.config.ReferenceConfig;

public class DubboBean extends AnnotationBean{
	private final ReferenceConfig<?> referenceConfig;
	
	public DubboBean(BeanFactory beanFactory, Class<?> type, ReferenceConfig<?> referenceConfig) throws Exception {
		super(beanFactory, type);
		this.referenceConfig = referenceConfig;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public boolean isProxy() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T newInstance() {
		referenceConfig.setInterface(getType());
		return (T) referenceConfig.get();// 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
	}
	
	@Override
	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		throw new NotSupportException(getType().getName());
	}
	
	@Override
	public void destroy(Object bean) {
		super.destroy(bean);
	}
}
