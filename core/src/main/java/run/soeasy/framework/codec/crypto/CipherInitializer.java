package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

/**
 * Cipher初始化器接口，定义在指定操作模式下初始化{@link Cipher}实例的规范，
 * 封装Cipher的初始化逻辑（如设置密钥、算法参数等），适配各类基于Cipher的加密/解密场景。
 * 
 * <p>该接口的实现类可根据具体加密算法（如AES、RSA等）和模式（如CBC、ECB等），
 * 在指定操作模式（加密/解密）下完成Cipher的初始化，使Cipher实例可直接用于数据处理。
 * 
 * @author soeasy.run
 * @see Cipher
 * @see GeneralSecurityException
 */
public interface CipherInitializer {

    /**
     * 初始化Cipher实例（指定操作模式）
     * 
     * @param cipher 待初始化的Cipher实例
     * @param opmode 操作模式（如{@link Cipher#ENCRYPT_MODE}加密模式、{@link Cipher#DECRYPT_MODE}解密模式）
     * @throws GeneralSecurityException 当初始化失败时抛出（如密钥错误、不支持的模式、参数不匹配等）
     */
    void init(Cipher cipher, int opmode) throws GeneralSecurityException;
}