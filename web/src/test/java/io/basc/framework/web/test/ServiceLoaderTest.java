package io.basc.framework.web.test;

import org.junit.Test;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.observe.register.ObservableServiceLoader;
import io.basc.framework.util.element.ServiceLoader;

public class ServiceLoaderTest {
	@Test
	public void test() {
		ServiceLoader<ClientHttpRequestFactory> loader = SPI.global().getServiceLoader(ClientHttpRequestFactory.class);
		ObservableServiceLoader<ClientHttpRequestFactory> serviceLoader = new ObservableServiceLoader<>();
		serviceLoader.registerServiceLoader(loader);

		ObservableServiceLoader<ClientHttpRequestFactory> serviceLoader1 = new ObservableServiceLoader<>();
		serviceLoader1.registerServiceLoader(serviceLoader);
		for (ClientHttpRequestFactory clientHttpRequestFactory : serviceLoader1.getServices()) {
			System.out.println(clientHttpRequestFactory);
		}
		System.out.println(serviceLoader1);
	}
}
