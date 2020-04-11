package scw.mvc.beans;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;

public class DefaultChannelBeanFactory extends AbstractChannelBeanFactory{
	private final BeanFactory beanFactory;
	private final Channel channel;
	
	public DefaultChannelBeanFactory(BeanFactory beanFactory, Channel channel) {
		this.beanFactory = beanFactory;
		this.channel = channel;
	}

	@Override
	public final BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public final Channel getChannel() {
		return channel;
	}

	@Override
	protected Object[] getBeanArgs(ParameterDescriptor[] parameterConfigs) {
		return MVCUtils.getParameterValues(channel, parameterConfigs);
	}

}
