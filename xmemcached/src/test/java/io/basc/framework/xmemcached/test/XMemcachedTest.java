package io.basc.framework.xmemcached.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.memcached.config.MemcachedNodeProperties;
import io.basc.framework.xmemcached.XMemcached;
import io.basc.framework.xmemcached.XMemcachedProperties;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class XMemcachedTest {

	public static void main(String[] args)
			throws IOException, TimeoutException, InterruptedException, MemcachedException {
		XMemcachedProperties properties = new XMemcachedProperties();
		properties.addNode(new MemcachedNodeProperties("localhost"));
		XMemcached memcached = new XMemcached(properties);
		memcached.set("test", "vvv", 2, TimeUnit.SECONDS);
		while (true) {
			System.out.println("----" + memcached.get("test") + "");
			memcached.getAndTouch("test", 2, TimeUnit.SECONDS);
			TimeUnit.SECONDS.sleep(2);
		}
	}
}
