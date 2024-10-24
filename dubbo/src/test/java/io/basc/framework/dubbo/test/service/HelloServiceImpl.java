package io.basc.framework.dubbo.test.service;

import io.basc.framework.context.annotation.Service;
import io.basc.framework.dubbo.test.reference.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String message) {
		return Thread.currentThread().getName() + "[" + message + "]";
	}

}
