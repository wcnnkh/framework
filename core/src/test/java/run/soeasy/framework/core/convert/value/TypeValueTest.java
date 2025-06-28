package run.soeasy.framework.core.convert.value;

import org.junit.Test;

public class TypeValueTest {
	@Test
	public void test() {
		System.out.println(TypedValue.of("111").getAsInt());
	}
}
