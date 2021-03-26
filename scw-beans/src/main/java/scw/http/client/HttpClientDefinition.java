package scw.http.client;

import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;

public class HttpClientDefinition extends DefaultBeanDefinition{

	public HttpClientDefinition(BeanFactory beanFactory) {
		super(beanFactory, DefaultHttpClient.class);
	}

	@Override
	public boolean isInstance() {
		return true;
	}
	
	@Override
	public Object create() throws InstanceException {
		return new DefaultHttpClient(beanFactory.getEnvironment(), beanFactory, beanFactory);
	}
}
