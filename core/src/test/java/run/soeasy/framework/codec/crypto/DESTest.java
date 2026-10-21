package run.soeasy.framework.codec.crypto;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.core.RandomUtils;

public class DESTest {
	public static String content = RandomUtils.uuid() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void test() {
		System.out.println("----------------BEGIN DES------------------");
		byte[] secreKey = charsetCodec.encode("12345678");
		byte[] iv = charsetCodec.encode("12345678");
		Codec<String, String> codec = charsetCodec.to(new DES(secreKey, iv)).to(HexCodec.DEFAULT);

		String encode = codec.encode(content);
		System.out.println("encode:" + encode);
		String decode = codec.decode(encode);
		System.out.println("decode:" + decode);
		assertTrue(decode.equals(content));
		System.out.println("----------------END DES------------------");
	}
}
