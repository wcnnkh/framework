package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;

/**
 * 映射容器实现，支持目标类型与源类型的注册映射转换。
 * <p>
 * 该容器继承自{@link MappedRegistry}，在类型映射注册中心的基础上，
 * 实现了{@link Container}接口，提供元素注册、注销和集合管理功能。
 * 通过编解码器{@link Codec}实现目标类型{T}与源类型{S}的双向转换，
 * 允许使用目标类型进行容器操作，内部自动转换为源类型存储。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射容器：通过编解码器实现目标类型与源类型的转换</li>
 *   <li>容器操作支持：提供元素注册、注销和集合查询功能</li>
 *   <li>注册状态透传：保持源容器的注册状态和异常上下文</li>
 *   <li>集合视图转换：返回转换后的目标类型元素集合</li>
 * </ul>
 *
 * @param <S> 源容器存储的类型（内部类型）
 * @param <T> 对外暴露的目标类型
 * @param <R> 源容器的注册类型，需继承{@link PayloadRegistration}&lt;{@link S}&gt;
 * @param <W> 被包装的源容器类型，需继承{@link Container}&lt;{@link S}, {@link R}&gt;
 * 
 * @author soeasy.run
 * @see MappedRegistry
 * @see Container
 */
public class MappedContainer<S, T, R extends PayloadRegistration<S>, W extends Container<S, R>>
        extends MappedRegistry<S, T, W> implements Container<T, PayloadRegistration<T>> {

    /**
     * 构造函数，初始化映射容器
     * <p>
     * 包装指定的源容器，并关联类型编解码器。
     * 
     * @param regisry 被包装的源容器，不可为null
     * @param codec 类型编解码器，负责T与S的双向转换，不可为null
     */
    public MappedContainer(W regisry, Codec<T, S> codec) {
        super(regisry, codec);
    }

    /**
     * 获取所有目标类型的有效注册
     * <p>
     * 将源容器中的源类型注册通过编解码器解码为目标类型，
     * 返回转换后的注册集合视图。
     * 
     * @return 目标类型的有效注册集合
     */
    @Override
    public Elements<PayloadRegistration<T>> getElements() {
        return getRegistry().getElements().map((e) -> e.map(getCodec()::decode));
    }

    /**
     * 批量注销目标类型元素
     * <p>
     * 将目标类型元素集合编码为源类型，调用源容器的注销方法，
     * 并返回注销操作的回执。
     * 
     * @param elements 要注销的目标类型元素集合，不可为null
     * @return 注销操作的回执，包含操作结果
     */
    @Override
    public Receipt deregisters(Elements<? extends T> elements) {
        Elements<S> target = elements.map(getCodec()::encode);
        return getRegistry().deregisters(target);
    }

    /**
     * 检查容器是否为空
     * <p>
     * 直接调用父类方法，检查源容器是否为空。
     * 
     * @return true表示容器中没有有效注册，false表示存在有效注册
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}