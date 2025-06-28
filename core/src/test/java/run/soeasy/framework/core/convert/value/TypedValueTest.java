package run.soeasy.framework.core.convert.value;

import org.junit.Test;

public class TypedValueTest {
	@Test
	public void test() {
		assert TypedValue.of("111").getAsInt() == 111;
	}
}
