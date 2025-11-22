package run.soeasy.framework.sequences;

import org.junit.Test;

public class LongCounterTest {
	@Test
	public void test() {
		AtomicLongCounter counter = new AtomicLongCounter();
		assert counter.next() == 1;
		Sequence<Long> sequence = counter.snapshot(2);
		assert counter.next() == 4;
		assert sequence.next() == 2;
	}
}
