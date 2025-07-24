package run.soeasy.framework.codec.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.sequences.UUIDSequence;

public class MessageDigestEncoderTest {
	public static String content = UUIDSequence.random().next() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void md5() {
		String msg = "md5";
		MessageDigestEncoder messageDigestEncoder = new MessageDigestEncoder(MessageDigestEncoder.MD5_ALGORITHM_NAME);
		messageDigestEncoder.setSecretKey(charsetCodec.encode(msg));
		Encoder<String, String> md5 = charsetCodec
				.toEncoder(messageDigestEncoder.toHex());
		String sign = md5.encode(msg);
		System.out.println("md5:" + sign);
		assertTrue(md5.test(msg, sign));
	}
}
