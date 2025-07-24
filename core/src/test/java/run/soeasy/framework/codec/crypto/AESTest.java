package run.soeasy.framework.codec.crypto;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.codec.format.HexCodec;
import run.soeasy.framework.sequences.UUIDSequence;

public class AESTest {
	public static String content = UUIDSequence.random().next() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void test() {
		System.out.println("----------------BEGIN AES------------------");
		byte[] secreKey = charsetCodec.encode("1234567812346578");
		byte[] iv = charsetCodec.encode("1234567812345678");
		Codec<String, String> codec = charsetCodec.to(new AES(secreKey, iv)).to(HexCodec.DEFAULT);
		String encode = codec.encode(content);
		System.out.println("encode:" + encode);
		String decode = codec.decode(encode);
		System.out.println("decode:" + decode);
		assertTrue(decode.equals(content));
		System.out.println("----------------END AES------------------");
	}
}
