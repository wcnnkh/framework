package io.basc.framework.netflix.eureka.server.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.boot.support.MainApplication;

public class EurekaServerStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		MainApplication.run(EurekaServerStart.class, new String[]{"-p", "8761"}).get();
	}
}
