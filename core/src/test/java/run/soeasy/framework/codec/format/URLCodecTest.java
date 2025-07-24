package run.soeasy.framework.codec.format;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.sequences.UUIDSequence;

public class URLCodecTest {
	public static String content = UUIDSequence.random().next() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void test() {
		System.out.println("----------------BEGIN multiple------------------");
		Codec<String, String> codec = URLCodec.UTF_8;
		String encode = codec.encode(content);
		System.out.println(encode);
		String decode = codec.decode(encode);
		System.out.println(decode);
		assertTrue(decode.equals(content));
		System.out.println("----------------multiple(10次)------------------");
		codec = URLCodec.UTF_8.multiple(10);
		encode = codec.encode(content);
		System.out.println(encode);
		decode = codec.decode(encode);
		System.out.println(decode);
		assertTrue(decode.equals(content));
		System.out.println("----------------END multiple------------------");
	}
}
