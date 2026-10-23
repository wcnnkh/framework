package run.soeasy.framework.codec.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.core.RandomUtils;

public class MessageDigestEncoderTest {
	public static String content = RandomUtils.uuid() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void md5() {
		String msg = "md5";
		MessageDigestEncoder messageDigestEncoder = new MessageDigestEncoder(MessageDigestEncoder.MD5_ALGORITHM_NAME);
		messageDigestEncoder.setSecretKey(charsetCodec.encode(msg));
		Coder<String, String> md5 = charsetCodec.encoder()
				.to(messageDigestEncoder.to(HexCodec.DEFAULT.encoder()));
		String sign = md5.apply(msg);
		System.out.println("md5:" + sign);
		assertTrue(md5.test(msg, sign));
	}
}
