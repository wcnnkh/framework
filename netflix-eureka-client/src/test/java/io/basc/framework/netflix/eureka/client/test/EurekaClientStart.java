package io.basc.framework.netflix.eureka.client.test;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.ApplicationUtils;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.netflix.eureka.EnableEurekaClient;
import io.basc.framework.netflix.eureka.EurekaDiscoveryClient;

@EnableEurekaClient
public class EurekaClientStart {
	private static Logger logger = LoggerFactory.getLogger(EurekaClientStart.class);
	@Autowired
	private Application application;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaClientStart.class, args).get();
		EurekaDiscoveryClient client = application.getBeanFactory().getInstance(EurekaDiscoveryClient.class);
		HttpClient httpClient = application.getBeanFactory().getInstance(HttpClient.class);
		while(true) {
			try {
				logger.info(client.getServices().toString());
				Thread.sleep(1000);
				HttpResponseEntity<String> response = httpClient.get(String.class, "http://" + ApplicationUtils.getApplicatoinName(application.getEnvironment()) + "/port");
				logger.info("测试请求返回：" + response);
			} catch (Exception e) {
				logger.error(e, "测试请求异常");
			}
		}
	}
	
	@Path("port")
	@GET
	public Object test(){
		return ApplicationUtils.getApplicationPort(application);
	}
}
