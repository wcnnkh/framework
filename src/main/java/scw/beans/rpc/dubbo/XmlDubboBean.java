package scw.beans.rpc.dubbo;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

import com.alibaba.dubbo.config.ReferenceConfig;

public final class XmlDubboBean extends AbstractInterfaceBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;
	private final BeanFactory beanFactory;

	public XmlDubboBean(BeanFactory beanFactory, Class<?> clz,
			ReferenceConfig<?> referenceConfig) {
		super(clz);
		this.referenceConfig = referenceConfig;
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) BeanUtils.proxyInterface(beanFactory, getInterfaceClass(),
				referenceConfig.get());
	}
}
