package run.soeasy.framework.codec.crypto;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.sequences.UUIDSequence;

public class HmacSHA1Test {
	public static String content = UUIDSequence.random().next() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void test() {
		Encoder<String, String> mac = charsetCodec.toEncoder(new HmacSHA1("1234".getBytes()).toHex());
		String sign = mac.encode(content);
		System.out.println("HmacSHA1:" + sign);
		assertTrue(mac.test(content, sign));
	}
}
