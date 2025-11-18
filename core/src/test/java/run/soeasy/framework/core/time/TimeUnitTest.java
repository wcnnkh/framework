package run.soeasy.framework.core.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class TimeUnitTest {

	@Test
	public void test() throws ParseException {
		Date time = new Date();
		assertEquals(TimeFormatter.format(time), time.toString());
		test(Year.DEFAULT, time);
		test(Month.DEFAULT, time);
		test(Day.DEFAULT, time);
		test(Hour.DEFAULT, time);
		test(Minute.DEFAULT, time);
		test(Second.DEFAULT, time);
		test(Millisecond.DEFAULT, time);
	}

	private void test(TimeUnitRange timeUnit, Date time) {
		assertTrue(timeUnit.minValue(time).getTime() == timeUnit.decode(timeUnit.encode(time)).getTime());
	}
}
