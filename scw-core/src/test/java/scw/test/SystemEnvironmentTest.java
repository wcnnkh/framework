package scw.test;

import org.junit.Test;

import scw.env.SystemEnvironment;

public class SystemEnvironmentTest {
	@Test
	public void init(){
		SystemEnvironment.getInstance();
	}
}
