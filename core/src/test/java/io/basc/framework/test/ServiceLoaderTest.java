package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.SpiServiceLoader;

public class ServiceLoaderTest {
	@Test
	public void test() {
		SpiServiceLoader<ClientHttpRequestFactory> loader = new SpiServiceLoader<>(ClientHttpRequestFactory.class);
		ServiceRegistry<ClientHttpRequestFactory> serviceLoader = new ServiceRegistry<>();
		serviceLoader.getServiceLoaderRegistry().register(loader);

		ServiceRegistry<ClientHttpRequestFactory> serviceLoader1 = new ServiceRegistry<>();
		serviceLoader1.getServiceLoaderRegistry().register(serviceLoader);
		for (ClientHttpRequestFactory clientHttpRequestFactory : serviceLoader1.getServices()) {
			System.out.println(clientHttpRequestFactory);
		}
		System.out.println(serviceLoader1);
	}
}
