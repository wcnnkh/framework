package scw.http.client;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.core.Ordered;

@Provider(order=Ordered.LOWEST_PRECEDENCE)
public class HttpClientBeanFactoryProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		HttpClientDefinition definition = new HttpClientDefinition(beanFactory);
		if(!beanFactory.containsDefinition(definition.getId())){
			beanFactory.registerDefinition(definition);
			
			if(!beanFactory.containsDefinition(HttpClient.class.getName())){
				beanFactory.registerAlias(HttpClient.class.getName(), definition.getId());;
			}
			
			if(!beanFactory.containsDefinition(HttpConnectionFactory.class.getName())){
				beanFactory.registerAlias(HttpConnectionFactory.class.getName(), definition.getId());
			}
		}
	}
	
}
