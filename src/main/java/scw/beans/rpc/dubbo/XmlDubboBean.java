package scw.beans.rpc.dubbo;

import java.util.Arrays;

import com.alibaba.dubbo.config.ReferenceConfig;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.common.exception.NotSupportException;

public final class XmlDubboBean implements BeanDefinition {
	private final ReferenceConfig<?> referenceConfig;
	private final BeanFactory beanFactory;

	public XmlDubboBean(BeanFactory beanFactory, ReferenceConfig<?> referenceConfig) {
		this.referenceConfig = referenceConfig;
		this.beanFactory = beanFactory;
	}

	public String getId() {
		return referenceConfig.getInterface();
	}

	public String[] getNames() {
		return new String[] { getId() };
	}

	public Class<?> getType() {
		return referenceConfig.getInterfaceClass();
	}

	public boolean isSingleton() {
		return true;
	}

	public boolean isProxy() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		return (T) BeanUtils.proxyInterface(beanFactory, getType(), referenceConfig.get());
	}

	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		throw new NotSupportException(Arrays.toString(parameterTypes));
	}

	public void autowrite(Object bean) throws Exception {
	}

	public void init(Object bean) throws Exception {
	}

	public void destroy(Object bean) throws Exception {
	}

	public <T> T newInstance(Object... params) {
		throw new NotSupportException(getType().getName());
	}
}
