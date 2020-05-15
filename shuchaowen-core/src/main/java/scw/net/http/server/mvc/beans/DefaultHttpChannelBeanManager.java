package scw.net.http.server.mvc.beans;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.MVCUtils;

public class DefaultHttpChannelBeanManager extends AbstractHttpChannelBeanManager{
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
	protected Object[] getBeanArgs(ParameterDescriptor[] parameterConfigs) {
		return MVCUtils.getParameterValues(httpChannel, parameterConfigs);
	}

}
