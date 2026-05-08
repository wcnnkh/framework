package run.soeasy.framework.codec.binary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.format.CharsetCodec;

public class GzipTest {
	@Test
	public void gzip() {
		String content = "a;dfkasdf;asfkasf;kasfsadkdkslslslsslslslsslllllllllllllllllllllllllllllllllllllllllllllllllllllld";
		String gzip = CharsetCodec.UTF_8.gzip().encode(content);
		System.out.println(gzip);
		assertTrue(content.equals(CharsetCodec.UTF_8.gzip().decode(gzip)));
	}
}
