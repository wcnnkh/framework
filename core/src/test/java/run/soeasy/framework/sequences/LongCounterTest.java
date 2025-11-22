package run.soeasy.framework.sequences;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class LongCounterTest {
	@Test
	public void test() {
		AtomicLongCounter counter = new AtomicLongCounter();
		System.out.println(counter.next());
		List<Long> list = counter.snapshot(10).stream().collect(Collectors.toList());
		System.out.println(list);
	}
}
