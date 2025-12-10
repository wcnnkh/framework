package run.soeasy.framework.core.exchange;

import javax.lang.model.util.Elements;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 批量可监听接口，定义支持批量事件注册和分发的能力。
 * 该接口继承自{@link Listenable}，但处理的事件类型为{@link Elements}，
 * 允许一次性注册和分发多个事件，提高处理效率。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量操作：通过Elements集合一次性处理多个事件</li>
 *   <li>模式转换：通过single()方法可转换为单条处理模式</li>
 *   <li>原子性保证：实现类可选择保证批量操作的原子性</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>批量事件注册和分发</li>
 *   <li>批处理模式的监听器管理</li>
 *   <li>需要事务保证的多事件处理</li>
 *   <li>性能敏感场景的事件合并处理</li>
 * </ul>
 *
 * @param <T> 监听的事件类型
 * 
 * @author soeasy.run
 * @see Listenable
 * @see Elements
 */
public interface BatchListenable<T> extends Listenable<Streamable<T>> {

    /**
     * 将当前批量可监听对象转换为单条处理模式
     * 默认实现返回一个假的单条可监听对象包装器，内部仍使用批量处理逻辑
     * 
     * @return 单条处理模式的可监听对象实例
     */
    default Listenable<T> single() {
        return (FakeSingleListenable<T, BatchListenable<T>>) (() -> this);
    }
}