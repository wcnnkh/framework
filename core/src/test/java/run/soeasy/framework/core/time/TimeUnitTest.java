package run.soeasy.framework.core.time;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeUnitTest {
	@Test
	public void test() {
		System.out.println(TimeUnit.MILLISECONDS.toSeconds(1));
	}
}
