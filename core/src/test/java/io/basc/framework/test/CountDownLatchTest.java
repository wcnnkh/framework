package io.basc.framework.test;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

public class CountDownLatchTest {
	@Test
	public void test(){
		CountDownLatch countDownLatch = new CountDownLatch(1);
		countDownLatch.countDown();
		countDownLatch.countDown();
		Assert.assertTrue(countDownLatch.getCount() == 0);
	}
}
