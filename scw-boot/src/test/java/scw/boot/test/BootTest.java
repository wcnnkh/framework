package scw.boot.test;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import scw.boot.support.MainApplication;

public class BootTest {
	@Test
	public void test() throws InterruptedException, ExecutionException{
		MainApplication.run(BootTest.class).get();
	}
}
