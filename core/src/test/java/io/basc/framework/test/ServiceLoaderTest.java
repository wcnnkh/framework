package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.factory.support.SpiServiceLoader;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.util.ServiceRegistry;

public class ServiceLoaderTest {
	@Test
	public void test() {
		SpiServiceLoader<ClientHttpRequestFactory> loader = new SpiServiceLoader<>(ClientHttpRequestFactory.class);
		ServiceRegistry<ClientHttpRequestFactory> serviceLoader = new ServiceRegistry<>();
		serviceLoader.getServiceLoaders().register(loader);

		ServiceRegistry<ClientHttpRequestFactory> serviceLoader1 = new ServiceRegistry<>();
		serviceLoader1.getServiceLoaders().register(serviceLoader);
		for (ClientHttpRequestFactory clientHttpRequestFactory : serviceLoader1.getServices()) {
			System.out.println(clientHttpRequestFactory);
		}
		System.out.println(serviceLoader1);
	}
}
