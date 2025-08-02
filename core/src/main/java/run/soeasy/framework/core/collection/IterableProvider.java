package run.soeasy.framework.core.collection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 可迭代对象的提供者实现，将任意Iterable转换为支持重载的元素集合。
 * 该类实现了ReloadableElementsWrapper接口，允许对底层可迭代对象进行刷新操作，
 * 并提供流式处理和集合操作能力。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式将普通Iterable转换为框架内的Elements接口</li>
 *   <li>支持重载机制，若底层对象实现了Reloadable接口则可触发刷新</li>
 *   <li>提供统一的元素集合操作接口，屏蔽底层实现差异</li>
 *   <li>所有操作默认委托给Elements.of(source)生成的元素集合</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要将普通集合转换为框架内Elements接口的场景</li>
 *   <li>需要对可迭代对象进行刷新操作的场景</li>
 *   <li>需要统一处理不同类型可迭代对象的场景</li>
 * </ul>
 *
 * @param <S> 元素类型
 * @see ReloadableElementsWrapper
 * @see Elements
 * @see Reloadable
 */
@RequiredArgsConstructor
public class IterableProvider<S> implements ReloadableElementsWrapper<S, Elements<S>> {

    /** 被包装的可迭代对象 */
    @NonNull
    private final Iterable<? extends S> source;

    /**
     * 获取被包装的元素集合。
     * 该方法将底层的可迭代对象转换为Elements接口实例。
     *
     * @return 元素集合实例
     */
    @Override
    public Elements<S> getSource() {
        return Elements.of(source);
    }

    /**
     * 重新加载数据。
     * 如果底层可迭代对象实现了Reloadable接口，则调用其reload方法；
     * 否则不执行任何操作。
     */
    @Override
    public void reload() {
        if (source instanceof Reloadable) {
            ((Reloadable) source).reload();
        }
    }
}