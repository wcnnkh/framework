package run.soeasy.framework.codec.security;

import java.security.GeneralSecurityException;

/**
 * 算法工厂接口，作为函数式接口用于创建安全算法实例（如加密算法、哈希算法等），
 * 是框架中安全算法实例化的标准化接口，负责封装算法的创建逻辑。
 * 
 * <p>该接口的实现类通常会处理算法的初始化参数（如密钥、模式、填充方式等），
 * 并返回可直接使用的算法实例，适用于各类需要动态创建安全算法的场景。
 * 
 * @param <T> 算法类型，如{@link javax.crypto.Cipher}、{@link java.security.MessageDigest}等
 * @author soeasy.run
 * @see GeneralSecurityException
 */
@FunctionalInterface
public interface AlgorithmFactory<T> {

    /**
     * 获取安全算法实例
     * 
     * @return 初始化完成的算法实例
     * @throws GeneralSecurityException 当算法创建或初始化失败时抛出（如不支持的算法、参数错误等）
     */
    T getAlgorithm() throws GeneralSecurityException;
}