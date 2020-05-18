package scw.mvc.beans;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.mvc.HttpChannel;
import scw.mvc.MVCUtils;

public class DefaultHttpChannelBeanManager extends AbstractHttpChannelBeanManager {
	private final BeanFactory beanFactory;
	private final HttpChannel httpChannel;

	public DefaultHttpChannelBeanManager(BeanFactory beanFactory, HttpChannel httpChannel) {
		this.beanFactory = beanFactory;
		this.httpChannel = httpChannel;
	}

	@Override
	public final BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public final HttpChannel getChannel() {
		return httpChannel;
	}

	@Override
	protected Object[] getBeanArgs(ParameterDescriptor[] parameterDescriptors) {
		return MVCUtils.getParameterValues(httpChannel, parameterDescriptors);
	}

}
