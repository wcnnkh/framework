package run.soeasy.framework.core.type;

import java.util.ArrayList;

import org.junit.Test;

public class ClassUtilsTest {
	@Test
	public void test() {
		ClassUtils.getInterfaces(ArrayList.class).pages().flatMap((e) -> e.getElements()).forEach(System.out::println);
	}
}
