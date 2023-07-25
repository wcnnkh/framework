package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.util.spi.Services;
import io.basc.framework.util.spi.SpiServiceLoader;

public class ServiceLoaderTest {
	@Test
	public void test() {
		SpiServiceLoader<ClientHttpRequestFactory> loader = new SpiServiceLoader<>(ClientHttpRequestFactory.class);
		Services<ClientHttpRequestFactory> serviceLoader = new Services<>();
		serviceLoader.getServiceLoaders().register(loader);

		Services<ClientHttpRequestFactory> serviceLoader1 = new Services<>();
		serviceLoader1.getServiceLoaders().register(serviceLoader);
		for (ClientHttpRequestFactory clientHttpRequestFactory : serviceLoader1.getServices()) {
			System.out.println(clientHttpRequestFactory);
		}
		System.out.println(serviceLoader1);
	}
}
