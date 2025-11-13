package run.soeasy.framework.core.math;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamIteratorTest {
	@Test
	public void test() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			list.add(ThreadLocalRandom.current().nextInt(100));
		}

		Stream<Integer> stream = list.stream().sorted();
		Iterator<Integer> iterator = stream.iterator();
		while (iterator.hasNext()) {
			Integer a = iterator.next();
			if (!iterator.hasNext()) {
				break;
			}
			Integer b = iterator.next();
			assertTrue(a <= b);
		}
	}
}
