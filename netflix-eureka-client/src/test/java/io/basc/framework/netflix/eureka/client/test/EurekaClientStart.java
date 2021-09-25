package io.basc.framework.netflix.eureka.client.test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.cloud.loadbalancer.Server;
import io.basc.framework.netflix.eureka.EnableEurekaClient;
import io.basc.framework.netflix.eureka.EurekaDiscoveryClient;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@EnableEurekaClient
public class EurekaClientStart {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(EurekaClientStart.class, new String[] {"-p", "8100"}).get();
		EurekaDiscoveryClient client = application.getBeanFactory().getInstance(EurekaDiscoveryClient.class);
		while(true) {
			System.out.println(client.getServices());
			Thread.sleep(1000);
			
			DiscoveryLoadBalancer discoveryLoadBalancer = application.getBeanFactory().getInstance(DiscoveryLoadBalancer.class);
			Server<ServiceInstance> server = discoveryLoadBalancer.choose((s) -> true);
			if(server != null){
				System.out.println(server.getService().getUri());
				System.out.println(server.getService().getHost());
				System.out.println(server.getService().getPort());
			}
		}
	}
	
	@Path("test")
	@GET
	public String test(){
		return "success";
	}
}
