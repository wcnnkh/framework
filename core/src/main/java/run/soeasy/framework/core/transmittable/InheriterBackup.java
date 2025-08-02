package run.soeasy.framework.core.transmittable;

import java.util.LinkedHashMap;

/**
 * 上下文备份容器，用于批量恢复多个{@link Inheriter}的上下文状态。
 * 该类继承自LinkedHashMap以保持Inheriter的注册顺序，
 * 确保恢复操作按与捕获时相反的顺序执行，保证上下文状态的正确恢复。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量恢复：支持同时恢复多个Inheriter的上下文状态</li>
 *   <li>顺序控制：按与捕获时相反的顺序执行恢复操作</li>
 *   <li>原子操作：提供统一的恢复方法，确保所有上下文一致恢复</li>
 *   <li>不可变性：备份后的数据不可修改，保证恢复的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>多维度上下文恢复：同时恢复线程本地变量、事务上下文等多种上下文</li>
 *   <li>批量上下文管理：在分布式链路追踪中批量恢复上下文状态</li>
 *   <li>跨线程上下文恢复：在线程池场景中统一恢复上下文状态</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建上下文捕获器
 * InheriterCapture&lt;TransmittableThreadLocal&lt;String&gt;, Void&gt; capture = new InheriterCapture&lt;&gt;();
 * capture.put(threadLocal1, threadLocal1.capture());
 * 
 * // 重放上下文并获取备份
 * InheriterBackup&lt;TransmittableThreadLocal&lt;String&gt;, Void&gt; backup = capture.replay();
 * try {
 *     // 执行需要上下文的业务逻辑
 * } finally {
 *     // 恢复所有上下文
 *     backup.restore();
 * }
 * </pre>
 *
 * @param <A> 捕获的上下文数据类型
 * @param <B> 备份的上下文数据类型
 * @see Inheriter
 * @see InheriterCapture
 */
public final class InheriterBackup<A, B> extends LinkedHashMap<Inheriter<A, B>, B> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建空的上下文备份容器，初始容量为16，加载因子为0.75。
     */
    public InheriterBackup() {
        super();
    }

    /**
     * 创建指定初始容量的上下文备份容器，加载因子为0.75。
     *
     * @param initialCapacity 初始容量，必须为非负数
     */
    public InheriterBackup(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 按与捕获时相反的顺序恢复所有备份的上下文状态。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>按当前存储的顺序遍历所有Inheriter和备份的上下文</li>
     *   <li>对每个Inheriter调用restore方法恢复上下文</li>
     *   <li>由于LinkedHashMap的顺序特性，实际恢复顺序与捕获顺序相反</li>
     * </ol>
     * <p>
     * 通常在finally块中调用此方法，确保无论业务逻辑执行结果如何，
     * 上下文状态都能被正确恢复到初始状态。
     */
    public void restore() {
        for (java.util.Map.Entry<Inheriter<A, B>, B> entry : entrySet()) {
            entry.getKey().restore(entry.getValue());
        }
    }
}