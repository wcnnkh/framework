package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.env.Sys;

public class SystemEnvironmentTest {
	@Test
	public void test() {
		boolean value = Sys.getEnv().getProperties().get("scw.test._key").or(true).getAsBoolean();
		assertTrue(value);
	}
}
