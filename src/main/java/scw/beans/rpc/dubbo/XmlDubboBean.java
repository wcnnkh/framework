package scw.beans.rpc.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public final class XmlDubboBean extends AbstractInterfaceBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;
	private final BeanFactory beanFactory;
	private final String[] filterNames;

	public XmlDubboBean(BeanFactory beanFactory, Class<?> clz, ReferenceConfig<?> referenceConfig,
			String[] filterNames) {
		super(clz);
		this.referenceConfig = referenceConfig;
		this.beanFactory = beanFactory;
		this.filterNames = filterNames;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) BeanUtils.proxyInterface(beanFactory, getInterfaceClass(), referenceConfig.get(), filterNames);
	}
}
