package io.basc.framework.netflix.eureka.server.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import com.netflix.eureka.EurekaServerConfig;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;

public class EurekaServerStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaServerStart.class, new String[]{"-p", "8761"}).get();
		EurekaServerConfig serverConfig = application.getInstance(EurekaServerConfig.class);
		assertTrue(serverConfig.shouldEnableSelfPreservation());
	}
}
