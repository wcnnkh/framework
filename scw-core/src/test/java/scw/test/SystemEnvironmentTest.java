package scw.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scw.env.SystemEnvironment;

public class SystemEnvironmentTest {
	@Test
	public void test(){
		boolean value = SystemEnvironment.getInstance().getValue("scw.test._key", boolean.class, true);
		assertTrue(value);
	}
}
