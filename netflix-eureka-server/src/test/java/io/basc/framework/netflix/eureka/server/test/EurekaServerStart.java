package io.basc.framework.netflix.eureka.server.test;

import io.basc.framework.boot.support.MainApplication;

public class EurekaServerStart {
	public static void main(String[] args) {
		MainApplication.run(EurekaServerStart.class, new String[]{"-p", "8761"});
	}
}
