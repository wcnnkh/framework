package run.soeasy.framework.io.source;

import run.soeasy.framework.core.domain.Wrapper;

import java.nio.charset.Charset;

/**
 * 字符集能力包装器接口，用于包装{@link CharsetAware}实例并委托所有操作，
 * 实现装饰器模式以支持对字符集能力的透明增强。该接口继承自{@link CharsetAware}和{@link Wrapper}，
 * 允许在不修改原始对象的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有字符集相关方法均转发给被包装的{@link CharsetAware}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、验证、缓存等额外功能</li>
 *   <li>类型安全：通过泛型确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式创建轻量级包装器</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>字符集使用日志记录：记录字符集获取操作的访问日志</li>
 *   <li>字符集验证增强：在获取字符集前进行合法性验证</li>
 *   <li>字符集缓存：缓存频繁访问的字符集信息</li>
 *   <li>多语言支持：根据上下文动态切换字符集</li>
 * </ul>
 *
 * @param <W> 被包装的字符集能力类型，必须是{@link CharsetAware}的子类型
 * @see CharsetAware
 * @see Wrapper
 */
@FunctionalInterface
public interface CharsetAwareWrapper<W extends CharsetAware> extends CharsetAware, Wrapper<W> {
    
    /**
     * 获取被包装对象使用的字符集，转发给被包装的CharsetAware实例。
     *
     * @return 被包装对象的字符集，不可为null
     * @see CharsetAware#getCharset()
     */
    @Override
    default Charset getCharset() {
        return getSource().getCharset();
    }

    /**
     * 获取被包装对象使用的字符集名称，转发给被包装的CharsetAware实例。
     *
     * @return 被包装对象的字符集规范名称
     * @see CharsetAware#getCharsetName()
     */
    @Override
    default String getCharsetName() {
        return getSource().getCharsetName();
    }
}