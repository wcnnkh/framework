package run.soeasy.framework.core.type;

import org.junit.Test;

public class InstanceFactorySupportedsTest {
	@Test
	public void test() {
		A a = (A) InstanceFactorySupporteds.REFLECTION.newInstance(ResolvableType.forType(A.class));
		System.out.println(a);
	}
	
	private static class A{
	}
}
