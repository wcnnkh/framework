package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;

import javax.crypto.Cipher;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 基于KeyFactory的Cipher初始化器，实现{@link CipherInitializer}接口，
 * 整合了KeyFactory获取、密钥（Key）生成及Cipher密钥初始化的完整流程，适用于需要通过KeyFactory动态生成密钥并初始化Cipher的场景。
 * 
 * <p>该类通过三个核心组件完成初始化：
 * 1. {@link #keyFactorySpi}：从Cipher获取KeyFactory实例的函数
 * 2. {@link #keySpi}：从KeyFactory生成密钥（Key）的函数
 * 3. {@link #cipherKeyInitializer}：使用生成的密钥初始化Cipher的处理器
 * 三者协作完成"KeyFactory获取→密钥生成→Cipher初始化"的链式流程。
 * 
 * @param <K> 密钥类型（需继承{@link Key}，如对称密钥、非对称密钥等）
 * @param <F> KeyFactory类型（需继承{@link KeyFactory}，用于生成密钥）
 * @param <E> 可能抛出的异常类型（需继承{@link GeneralSecurityException}）
 * @author soeasy.run
 * @see CipherInitializer
 * @see KeyFactory
 * @see CipherKeyInitializer
 */
@Getter
@RequiredArgsConstructor
public class UsingKeyFactoryCipherInitializer<K extends Key, F extends KeyFactory, E extends GeneralSecurityException>
		implements CipherInitializer {

    /**
     * 用于从Cipher实例获取KeyFactory的函数，负责KeyFactory的实例化逻辑
     */
	@NonNull
	private final ThrowingFunction<? super Cipher, ? extends F, ? extends E> keyFactorySpi;

    /**
     * 用于从KeyFactory生成密钥（Key）的函数，负责密钥的生成逻辑
     */
	@NonNull
	private final ThrowingFunction<? super F, ? extends K, ? extends E> keySpi;

    /**
     * 用于使用生成的密钥初始化Cipher的处理器，封装密钥与Cipher的绑定逻辑
     */
	@NonNull
	private final CipherKeyInitializer<? super K> cipherKeyInitializer;

    /**
     * 初始化Cipher实例（通过KeyFactory生成密钥并绑定）
     * 
     * <p>处理流程：
     * 1. 调用{@link #keyFactorySpi}从Cipher获取KeyFactory实例
     * 2. 调用{@link #keySpi}从KeyFactory生成密钥（Key）
     * 3. 调用{@link #cipherKeyInitializer}使用生成的密钥初始化Cipher（指定操作模式）
     * 
     * @param cipher 待初始化的Cipher实例
     * @param opmode 操作模式（如{@link Cipher#ENCRYPT_MODE}加密模式、{@link Cipher#DECRYPT_MODE}解密模式）
     * @throws GeneralSecurityException 当初始化过程失败时抛出（如KeyFactory获取失败、密钥生成错误、Cipher绑定密钥失败等）
     */
	@Override
	public void init(Cipher cipher, int opmode) throws GeneralSecurityException {
		F keyFactory = keyFactorySpi.apply(cipher);
		K key = keySpi.apply(keyFactory);
		cipherKeyInitializer.init(cipher, opmode, key);
	}
}