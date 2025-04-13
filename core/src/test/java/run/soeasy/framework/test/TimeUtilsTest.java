package run.soeasy.framework.test;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import run.soeasy.framework.core.time.TimeUtils;

public class TimeUtilsTest {

	@Test
	public void test() throws ParseException {
		long time = System.currentTimeMillis();
		test(TimeUtils.YEAR, time);
		test(TimeUtils.MONTH, time);
		test(TimeUtils.DAY, time);
		test(TimeUtils.HOUR, time);
		test(TimeUtils.MINUTE, time);
		test(TimeUtils.SECOND, time);
		test(TimeUtils.MILLISECOND, time);

		String str = new Date(time).toString();
		assertTrue(time / 1000 == TimeUtils.convert(str).getTime() / 1000);
	}

	private void test(TimeUtils timeUtils, long time) {
		assertTrue(timeUtils.getMinTime(time) == timeUtils.parse(timeUtils.format(time)));
		assertTrue(timeUtils.getMinTime(time) == TimeUtils.convert(timeUtils.format(time)).getTime());
	}
}
