package run.soeasy.framework.sequences;

import org.junit.Test;

public class SnowflakeSequenceTest {
	@Test
	public void test() {
		SnowflakeSequence sequence = SnowflakeSequence.create();
		System.out.println(sequence.next());
	}
}
