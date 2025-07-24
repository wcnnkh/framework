package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

/**
 * 带密钥的Cipher初始化器接口，扩展了{@link CipherInitializer}功能，支持在初始化{@link Cipher}实例时指定密钥，
 * 适用于需要动态传入密钥的加密/解密场景（如对称钥轮换、多密钥体系等）。
 * 
 * <p>该接口的实现类可根据密钥类型（如对称密钥SecretKey、非对称钥PublicKey/PrivateKey等），
 * 在指定操作模式下完成Cipher的初始化，使Cipher实例与特定密钥绑定，直接用于数据处理。
 * 
 * @param <K> 密钥类型（如{@link javax.crypto.SecretKey}、{@link java.security.PublicKey}等）
 * @author soeasy.run
 * @see Cipher
 * @see CipherInitializer
 */
public interface CipherKeyInitializer<K> {

    /**
     * 使用指定密钥初始化Cipher实例（指定操作模式）
     * 
     * @param cipher 待初始化的Cipher实例
     * @param opmode 操作模式（如{@link Cipher#ENCRYPT_MODE}加密模式、{@link Cipher#DECRYPT_MODE}解密模式）
     * @param key 用于初始化的密钥（类型由泛型K指定）
     * @throws GeneralSecurityException 当初始化失败时抛出（如密钥不匹配、不支持的密钥类型、模式错误等）
     */
    void init(Cipher cipher, int opmode, K key) throws GeneralSecurityException;
}