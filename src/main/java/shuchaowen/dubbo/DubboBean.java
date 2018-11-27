package shuchaowen.dubbo;

import com.alibaba.dubbo.config.ReferenceConfig;

import shuchaowen.core.beans.AbstractInterfaceProxyBean;

public class DubboBean extends AbstractInterfaceProxyBean{
	private final ReferenceConfig<?> referenceConfig;
	
	public DubboBean(Class<?> type, ReferenceConfig<?> referenceConfig) throws Exception {
		super(type);
		this.referenceConfig = referenceConfig;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		referenceConfig.setInterface(getType());
		return (T) referenceConfig.get();// 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
	}
}
