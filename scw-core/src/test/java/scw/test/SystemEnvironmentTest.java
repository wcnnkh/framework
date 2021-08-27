package scw.test;

import static org.junit.Assert.assertTrue;
import io.basc.framework.env.Sys;

import org.junit.Test;

public class SystemEnvironmentTest {
	@Test
	public void test(){
		boolean value = Sys.env.getValue("scw.test._key", boolean.class, true);
		assertTrue(value);
	}
}
