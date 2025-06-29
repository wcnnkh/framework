package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 注册表接口
 * 定义元素注册和管理的标准行为，继承自Elements接口
 * 
 * @author soeasy.run
 *
 * @param <E> 注册表中存储的元素类型
 */
public interface Registry<E> extends Elements<E> {
    /**
     * 转换注册表元素类型
     * 
     * @param codec 类型转换编解码器，不可为null
     * @param <R> 目标元素类型
     * @return 转换后的新注册表
     */
    default <R> Registry<R> map(@NonNull Codec<R, E> codec) {
        return new MappedRegistry<>(this, codec);
    }

    /**
     * 注册单个元素
     * 
     * @param element 待注册的元素
     * @return 注册操作的句柄
     * @throws RegistrationException 注册失败时抛出
     */
    default Registration register(E element) throws RegistrationException {
        return registers(Elements.singleton(element));
    }

    /**
     * 批量注册元素
     * 
     * @param elements 待注册的元素集合，不可为null
     * @return 注册操作的句柄
     * @throws RegistrationException 注册失败时抛出
     */
    Registration registers(@NonNull Elements<? extends E> elements) throws RegistrationException;
}