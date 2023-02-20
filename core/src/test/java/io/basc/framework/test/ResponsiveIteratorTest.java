package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import io.basc.framework.util.ResponsiveIterator;
import io.basc.framework.util.XUtils;

public class ResponsiveIteratorTest {
	private static List<Object> pushList = new ArrayList<Object>();

	@Test
	public void test() throws InterruptedException {
		ResponsiveIterator<Object> iterator = new ResponsiveIterator<>();
		new PushThread(iterator).start();
		assertTrue(pushList.equals(XUtils.stream(iterator).collect(Collectors.toList())));
	}

	private static class PushThread extends Thread {
		private ResponsiveIterator<Object> iterator;
		private int count = 0;

		public PushThread(ResponsiveIterator<Object> iterator) {
			this.iterator = iterator;
		}

		@Override
		public void run() {
			while (++count < 100) {
				String uuid = XUtils.getUUID();
				try {
					iterator.put(uuid);
					pushList.add(uuid);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			iterator.close();
		}
	}
}
