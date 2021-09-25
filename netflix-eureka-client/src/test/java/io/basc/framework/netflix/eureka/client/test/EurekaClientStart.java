package io.basc.framework.netflix.eureka.client.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.netflix.eureka.EurekaDiscoveryClient;

public class EurekaClientStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaClientStart.class, new String[] {"-p", "8100"}).get();
		EurekaDiscoveryClient client = application.getBeanFactory().getInstance(EurekaDiscoveryClient.class);
		while(true) {
			System.out.println(client.getServices());
			Thread.sleep(1000);
		}
		
	}
}
