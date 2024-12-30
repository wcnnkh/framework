package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.Test;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.Encoder;
import io.basc.framework.util.codec.encode.MD5;
import io.basc.framework.util.codec.encode.SHA1WithRSASigner;
import io.basc.framework.util.codec.support.AES;
import io.basc.framework.util.codec.support.Base64;
import io.basc.framework.util.codec.support.CharsetCodec;
import io.basc.framework.util.codec.support.DES;
import io.basc.framework.util.codec.support.HexCodec;
import io.basc.framework.util.codec.support.RSA;
import io.basc.framework.util.codec.support.URLCodec;
import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class CodecTest {
	public static String content = UUIDSequences.getUUID() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void des() {
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

	@Test
	public void aes() {
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

	@Test
	public void rsa() throws NoSuchAlgorithmException {
		System.out.println("----------------BEGIN RSA------------------");
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥
		RSA rsa = new RSA(publicKey, privateKey, 128);
		Codec<String, String> codec = charsetCodec.to(rsa).to(Base64.DEFAULT);
		String encode = codec.encode(content);
		System.out.println("encode:" + encode);
		String decode = codec.decode(encode);
		System.out.println("decode:" + decode);
		assertTrue(decode.equals(content));
		System.out.println("----------------END RSA------------------");

		SHA1WithRSASigner rsaSigner = new SHA1WithRSASigner(privateKey, publicKey);
		Encoder<String, String> encoder = charsetCodec.toEncoder(rsaSigner).toEncoder(Base64.DEFAULT);
		String sign = encoder.encode(content);
		System.out.println("sign:" + sign);
		assertTrue(encoder.verify(content, sign));
	}

	@Test
	public void multiple() {
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

	@Test
	public void md5() {
		String msg = "md5";
		Encoder<String, String> md5 = charsetCodec
				.toEncoder(new MD5().wrapperSecretKey(charsetCodec.encode(msg)).toHex());
		String sign = md5.encode(msg);
		System.out.println("md5:" + sign);
		assertTrue(md5.verify(msg, sign));
	}

	@Test
	public void HmacSHA1() {
		Encoder<String, String> mac = charsetCodec
				.toEncoder(new io.basc.framework.util.codec.encode.HmacSHA1("1234".getBytes()).toHex());
		String sign = mac.encode(content);
		System.out.println("HmacSHA1:" + sign);
		assertTrue(mac.verify(content, sign));
	}

	@Test
	public void HmacMD5() {
		Encoder<String, String> mac = charsetCodec
				.toEncoder(new io.basc.framework.util.codec.encode.HmacMD5("1234".getBytes()).toHex());
		String sign = mac.encode(content);
		System.out.println("HmacMD5:" + sign);
		assertTrue(mac.verify(content, sign));
	}
}
