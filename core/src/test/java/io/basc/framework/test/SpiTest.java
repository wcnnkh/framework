package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.util.element.ServiceLoader;

public class SpiTest implements SpiTestInterface {
	private static final String TEST_TEXT = "hello";

	@Override
	public String test() {
		return TEST_TEXT;
	}

	@Test
	public void run() {
		ServiceLoader<SpiTestInterface> spiServiceLoader = SPI.global().getServiceLoader(SpiTestInterface.class);
		SpiTestInterface spiTestInterface = spiServiceLoader.getServices().first();
		String text = spiTestInterface.test();
		assertTrue(TEST_TEXT.equals(text));
	}
}
