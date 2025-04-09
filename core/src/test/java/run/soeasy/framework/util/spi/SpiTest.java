package run.soeasy.framework.util.spi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.util.collection.Provider;

public class SpiTest implements SpiTestInterface {
	private static final String TEST_TEXT = "hello";

	@Override
	public String test() {
		return TEST_TEXT;
	}

	@Test
	public void run() {
		Provider<SpiTestInterface> spiServiceLoader = NativeServiceLoader.load(SpiTestInterface.class);
		SpiTestInterface spiTestInterface = spiServiceLoader.first();
		String text = spiTestInterface.test();
		assertTrue(TEST_TEXT.equals(text));
	}
}
