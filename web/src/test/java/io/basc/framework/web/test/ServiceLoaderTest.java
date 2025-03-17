package io.basc.framework.web.test;

import org.junit.Test;

import run.soeasy.framework.beans.factory.spi.SPI;
import run.soeasy.framework.http.client.ClientHttpRequestFactory;
import run.soeasy.framework.observe.service.ObservableServiceLoader;
import run.soeasy.framework.util.collections.ServiceLoader;

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
