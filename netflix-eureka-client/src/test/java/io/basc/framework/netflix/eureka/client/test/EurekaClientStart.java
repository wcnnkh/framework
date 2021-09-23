package io.basc.framework.netflix.eureka.client.test;

import io.basc.framework.boot.support.MainApplication;

public class EurekaClientStart {
	public static void main(String[] args) {
		MainApplication.run(EurekaClientStart.class, new String[] {"-p", "8100"});
	}
}
