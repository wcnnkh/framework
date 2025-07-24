package run.soeasy.framework.core.exchange.container;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 映射注册中心，实现源类型与目标类型的注册映射转换。
 * <p>
 * 该类通过编解码器{@link Codec}实现源类型{S}与目标类型{T}的转换，
 * 允许使用目标类型进行注册操作，内部自动转换为源类型存储。
 * 实现了{@link Registry}和{@link ElementsWrapper}接口，
 * 支持注册管理和元素集合的包装转换。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射注册：通过编解码器实现类型转换的注册操作</li>
 *   <li>双向转换：支持目标类型到源类型的编码，源类型到目标类型的解码</li>
 *   <li>集合包装：提供转换后的元素集合视图</li>
 *   <li>异常透明：注册过程中的异常直接透传，保持异常上下文</li>
 * </ul>
 *
 * @param <S> 源注册中心存储的类型（内部类型）
 * @param <T> 对外暴露的注册类型（目标类型）
 * @param <W> 被包装的源注册中心类型，需继承{@link Registry}&lt;{@link S}&gt;
 * 
 * @author soeasy.run
 * @see Registry
 * @see Codec
 * @see ElementsWrapper
 */
@Data
public class MappedRegistry<S, T, W extends Registry<S>> implements Registry<T>, ElementsWrapper<T, Elements<T>> {
    
    /** 被包装的源注册中心，存储源类型{S}的注册信息 */
    @NonNull
    private final W registry;
    
    /** 类型编解码器，负责目标类型{T}与源类型{S}的转换 */
    @NonNull
    private final Codec<T, S> codec;

    /**
     * 注册目标类型元素
     * <p>
     * 将目标类型元素通过编解码器编码为源类型，然后调用源注册中心的注册方法。
     * 注册异常直接透传，保持原始异常信息。
     * 
     * @param element 要注册的目标类型元素，不可为null
     * @return 注册结果，包含注册状态和元数据
     * @throws RegistrationException 注册失败时抛出，包含源注册中心的异常信息
     */
    @Override
    public Registration register(T element) throws RegistrationException {
        S target = codec.encode(element);
        return registry.register(target);
    }

    /**
     * 获取转换后的元素集合
     * <p>
     * 将源注册中心的源类型元素集合通过编解码器解码为目标类型，
     * 返回转换后的元素集合视图。
     * 
     * @return 解码后的目标类型元素集合
     */
    @Override
    public Elements<T> getSource() {
        return registry.map(codec::decode);
    }

    /**
     * 批量注册目标类型元素集合
     * <p>
     * 将目标类型元素集合批量编码为源类型，然后调用源注册中心的批量注册方法。
     * 注册异常直接透传，保持原始异常信息。
     * 
     * @param elements 要注册的目标类型元素集合，不可为null
     * @return 批量注册结果，包含所有注册状态
     * @throws RegistrationException 批量注册失败时抛出，包含源注册中心的异常信息
     */
    @Override
    public Registration registers(Elements<? extends T> elements) throws RegistrationException {
        Elements<S> target = elements.map(codec::encode);
        return registry.registers(target);
    }
}