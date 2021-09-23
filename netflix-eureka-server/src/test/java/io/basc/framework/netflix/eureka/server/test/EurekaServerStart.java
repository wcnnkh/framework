package io.basc.framework.netflix.eureka.server.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.netflix.eureka.server.EurekaServerConfigBean;

public class EurekaServerStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaServerStart.class, new String[]{"-p", "8761"}).get();
		EurekaServerConfigBean eurekaServerConfigBean = application.getBeanFactory().getInstance(EurekaServerConfigBean.class);
		System.out.println(eurekaServerConfigBean);
	}
}
