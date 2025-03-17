package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import run.soeasy.framework.util.RandomUtils;

public class RandomTest {
	@Test
	public void test() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(10);
		list.add(1);
		list.add(3);

		for (int i = 0; i < 1000000; i++) {
			assertTrue(RandomUtils.random(list, (e) -> e, null) != null);
		}
	}
}
