package io.basc.framework.xmemcached.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.xmemcached.XMemcached;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class XMemcachedTest {

	public static void main(String[] args)
			throws IOException, TimeoutException, InterruptedException, MemcachedException {
		XMemcached memcached = new XMemcached("localhost:11211");
		memcached.set("test", "vvv", 2, TimeUnit.SECONDS);
		while (true) {
			System.out.println("----" + memcached.get("test") + "");
			memcached.getAndTouch("test", 2, TimeUnit.SECONDS);
			TimeUnit.SECONDS.sleep(2);
		}
	}
}
