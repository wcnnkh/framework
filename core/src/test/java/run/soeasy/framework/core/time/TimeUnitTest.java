package run.soeasy.framework.core.time;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class TimeUnitTest {

	@Test
	public void test() throws ParseException {
		Date time = new Date();
		test(Year.DEFAULT, time);
		test(Month.DEFAULT, time);
		test(Day.DEFAULT, time);
		test(Hour.DEFAULT, time);
		test(Minute.DEFAULT, time);
		test(Second.DEFAULT, time);
		test(Millisecond.DEFAULT, time);

		String str = time.toString();
		assertTrue((time.getTime() / 1000) == (TimeFormatter.parse(str).getTime() / 1000));
	}

	private void test(TimeUnit timeUnit, Date time) {
		System.out.println(timeUnit.minValue(time) + "    " + timeUnit.format(time));
		assertTrue(timeUnit.minValue(time).getTime() == timeUnit.parse(timeUnit.format(time)).getTime());
	}
}
