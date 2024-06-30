package io.basc.framework.web.test;

import org.junit.Test;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.ServiceLoader;

public class ServiceLoaderTest {
	@Test
	public void test() {
		ServiceLoader<ClientHttpRequestFactory> loader = SPI.global().getServiceLoader(ClientHttpRequestFactory.class);
		ServiceRegistry<ClientHttpRequestFactory> serviceLoader = new ServiceRegistry<>();
		serviceLoader.registerServiceLoader(loader);

		ServiceRegistry<ClientHttpRequestFactory> serviceLoader1 = new ServiceRegistry<>();
		serviceLoader1.registerServiceLoader(serviceLoader);
		for (ClientHttpRequestFactory clientHttpRequestFactory : serviceLoader1.getServices()) {
			System.out.println(clientHttpRequestFactory);
		}
		System.out.println(serviceLoader1);
	}
}
