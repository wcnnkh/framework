package run.soeasy.framework.codec.security;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.Test;

import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.CharsetCodec;
import run.soeasy.framework.core.RandomUtils;

public class SignatureEncoderTest {
	public static String content = RandomUtils.uuid() + "这是一段加解密测试内容!";
	public static CharsetCodec charsetCodec = CharsetCodec.UTF_8;

	@Test
	public void sha1WithRSA() throws NoSuchAlgorithmException {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥
		SignatureEncoder rsaSigner = new SignatureEncoder(SignatureEncoder.SHA1_WITH_RSA, privateKey, publicKey);
		Encoder<String, String> encoder = charsetCodec.toEncoder(rsaSigner).toEncoder(Base64.DEFAULT);
		String sign = encoder.encode(content);
		System.out.println("sign:" + sign);
		assertTrue(encoder.test(content, sign));
	}
}
