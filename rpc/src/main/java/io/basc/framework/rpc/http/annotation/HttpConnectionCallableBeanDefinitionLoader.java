package io.basc.framework.rpc.http.annotation;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;

import javax.ws.rs.Path;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class HttpConnectionCallableBeanDefinitionLoader implements BeanDefinitionLoader {

	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain) {
		HttpRemote remote = sourceClass.getAnnotation(HttpRemote.class);
		Path path = sourceClass.getAnnotation(Path.class);
		if (remote == null && path == null) {
			return loaderChain.load(beanFactory, sourceClass);
		}
		
		HttpClient httpClient = getHttpClient(beanFactory);
		CallableFactory callableFactory;
		if (remote != null) {
			callableFactory = new AnnotationHttpCallableFactory(httpClient, remote,
					beanFactory.getDefaultValueFactory());
		} else {
			callableFactory = new AnnotationHttpCallableFactory(httpClient, path,
					beanFactory.getDefaultValueFactory());
		}
		return new RemoteCallableBeanDefinition(beanFactory, callableFactory, sourceClass);
	}
	
	private HttpClient getHttpClient(BeanFactory beanFactory){
		if(beanFactory.isInstance(HttpClient.class)){
			return beanFactory.getInstance(HttpClient.class);
		}else{
			return HttpUtils.getHttpClient();
		}
	}

}
