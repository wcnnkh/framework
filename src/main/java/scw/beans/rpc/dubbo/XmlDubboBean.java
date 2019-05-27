package scw.beans.rpc.dubbo;

import com.alibaba.dubbo.config.ReferenceConfig;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.rpc.AbstractInterfaceProxyBean;

public final class XmlDubboBean extends AbstractInterfaceProxyBean {
	private final ReferenceConfig<?> referenceConfig;
	private final BeanFactory beanFactory;

	public XmlDubboBean(BeanFactory beanFactory, Class<?> clz, ReferenceConfig<?> referenceConfig) throws Exception {
		super(clz);
		this.referenceConfig = referenceConfig;
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) BeanUtils.proxyInterface(beanFactory, getType(), referenceConfig.get());
	}
}
