package scw.test;

import org.junit.Test;

import scw.env.SystemEnvironment;

public class TestSystemEnvironment {
	@Test
	public void init(){
		SystemEnvironment.getInstance();
	}
}
