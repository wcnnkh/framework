package io.basc.framework.rpc.test;

import io.basc.framework.rpc.http.annotation.HttpRemote;
import io.basc.framework.web.pattern.annotation.RequestMapping;

@HttpRemote("http://127.0.0.1:${server.port}")
public interface TestRemoteInterface {
	@RequestMapping
	String index();
}
