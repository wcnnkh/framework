package io.basc.framework.rpc.test;

import io.basc.framework.rpc.http.annotation.HttpRemote;
import io.basc.framework.web.pattern.annotation.RequestMapping;

@HttpRemote("https://www.baidu.com")
public interface TestRemoteInterface {
	@RequestMapping
	String index();
}
