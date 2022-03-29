package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.TimeUtils;

public class TimeUtilsTest {

	@Test
	public void test() {
		long time = System.currentTimeMillis();
		test(TimeUtils.YEAR, time);
		test(TimeUtils.MONTH, time);
		test(TimeUtils.DAY, time);
		test(TimeUtils.HOUR, time);
		test(TimeUtils.MINUTE, time);
		test(TimeUtils.SECOND, time);
		test(TimeUtils.MILLISECOND, time);

	}

	private void test(TimeUtils timeUtils, long time) {
		assertTrue(timeUtils.getMinTime(time) == timeUtils.getTime(timeUtils.format(time)));
	}
}
