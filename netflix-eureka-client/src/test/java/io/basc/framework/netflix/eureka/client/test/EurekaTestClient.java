package io.basc.framework.netflix.eureka.client.test;

import io.basc.framework.rpc.http.annotation.HttpRemote;
import io.basc.framework.web.pattern.annotation.RequestMapping;

@HttpRemote("http://eureka-test-client")
public interface EurekaTestClient {
	@RequestMapping("port")
	String port();
}
