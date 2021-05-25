package scw.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scw.env.Sys;

public class SystemEnvironmentTest {
	@Test
	public void test(){
		boolean value = Sys.env.getValue("scw.test._key", boolean.class, true);
		assertTrue(value);
	}
}
