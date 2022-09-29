package io.basc.framework.test;

import io.basc.framework.factory.support.SpiServiceLoader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SpiTest implements SpiTestInterface {
	private static final String TEST_TEXT = "hello";
	
	@Override
	public String test() {
		return TEST_TEXT;
	}

	@Test
	public void run(){
		SpiServiceLoader<SpiTestInterface> spiServiceLoader = new SpiServiceLoader<SpiTestInterface>(SpiTestInterface.class);
		SpiTestInterface spiTestInterface = spiServiceLoader.first();
		String text = spiTestInterface.test();
		assertTrue(TEST_TEXT.equals(text));
	}
}
