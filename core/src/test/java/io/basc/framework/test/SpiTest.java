package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.spi.SpiServiceLoader;

public class SpiTest implements SpiTestInterface {
	private static final String TEST_TEXT = "hello";

	@Override
	public String test() {
		return TEST_TEXT;
	}

	@Test
	public void run() {
		SpiServiceLoader<SpiTestInterface> spiServiceLoader = new SpiServiceLoader<SpiTestInterface>(
				SpiTestInterface.class);
		SpiTestInterface spiTestInterface = spiServiceLoader.getServices().first();
		String text = spiTestInterface.test();
		assertTrue(TEST_TEXT.equals(text));
	}
}
