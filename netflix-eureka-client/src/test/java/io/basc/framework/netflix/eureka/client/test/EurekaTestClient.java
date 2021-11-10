package io.basc.framework.netflix.eureka.client.test;

import javax.ws.rs.QueryParam;

import io.basc.framework.rpc.http.annotation.HttpRemote;
import io.basc.framework.web.pattern.annotation.RequestMapping;

@HttpRemote("http://eureka-test-client")
public interface EurekaTestClient {
	@RequestMapping("port")
	String port();
	
	@RequestMapping("message")
	String message(@QueryParam("message") String message);
}
