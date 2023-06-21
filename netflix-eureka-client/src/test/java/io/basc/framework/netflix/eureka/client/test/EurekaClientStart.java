package io.basc.framework.netflix.eureka.client.test;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.context.annotation.Autowired;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.netflix.eureka.boot.EnableEurekaClient;
import io.basc.framework.util.XUtils;

@EnableEurekaClient
@Path("/")
public class EurekaClientStart {
	private static Logger logger = LoggerFactory.getLogger(EurekaClientStart.class);
	@Autowired
	private Application application;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaClientStart.class, args).get();
		DiscoveryLoadBalancer client = application.getInstance(DiscoveryLoadBalancer.class);
		HttpClient httpClient = application.getInstance(HttpClient.class);
		EurekaTestClient eurekaTestClient = application.getInstance(EurekaTestClient.class);
		while (true) {
			try {
				logger.info(client.toString());
				Thread.sleep(1000);
				if (client.isEmpty()) {
					continue;
				}
				HttpResponseEntity<String> response = httpClient.get(String.class,
						"http://" + application.getName().get() + "/port");
				logger.info("测试请求1返回：" + response);

				String port = eurekaTestClient.message(XUtils.getUUID());
				logger.info("测试请求2返回:" + port);
			} catch (Exception e) {
				logger.error(e, "测试请求异常");
			}
		}
	}

	@Path("/port")
	@GET
	public Object test() {
		return application.getPort();
	}

	@Path("message")
	@GET
	public String message(String message) {
		return message;
	}
}
