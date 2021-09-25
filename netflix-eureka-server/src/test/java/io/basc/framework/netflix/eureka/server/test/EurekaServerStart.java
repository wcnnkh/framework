package io.basc.framework.netflix.eureka.server.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.netflix.eureka.EurekaDiscoveryClient;

public class EurekaServerStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaServerStart.class, new String[]{"-p", "8761"}).get();
		EurekaDiscoveryClient eurekaDiscoveryClient = application.getBeanFactory().getInstance(EurekaDiscoveryClient.class);
		while(true) {
			System.out.println(eurekaDiscoveryClient.getServices());
			Thread.sleep(1000L);
		}
	}
}
