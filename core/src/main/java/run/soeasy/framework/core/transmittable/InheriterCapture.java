package run.soeasy.framework.core.transmittable;

import java.util.LinkedHashMap;

/**
 * 上下文捕获容器，用于批量捕获多个{@link Inheriter}的上下文状态，
 * 并支持按顺序重放捕获的上下文到目标环境。
 * 该类继承自LinkedHashMap以保持Inheriter的注册顺序，
 * 确保重放和恢复操作按注册顺序执行，保证上下文传递的一致性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>批量捕获：支持同时捕获多个Inheriter的上下文状态</li>
 *   <li>顺序保持：使用LinkedHashMap维持Inheriter的注册顺序</li>
 *   <li>原子重放：将所有捕获的上下文一次性重放到目标环境</li>
 *   <li>上下文备份：重放后返回统一的备份对象用于后续恢复</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>多维度上下文传递：同时传递线程本地变量、事务上下文等多种上下文</li>
 *   <li>批量上下文管理：在分布式链路追踪中批量处理上下文传递</li>
 *   <li>跨线程上下文传递：在线程池场景中统一管理上下文的捕获和恢复</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建上下文捕获器
 * InheriterCapture&lt;TransmittableThreadLocal&lt;String&gt;, Void&gt; capture = new InheriterCapture&lt;&gt;();
 * 
 * // 注册多个Inheriter
 * capture.put(threadLocal1, threadLocal1.capture());
 * capture.put(threadLocal2, threadLocal2.capture());
 * 
 * // 重放上下文并获取备份
 * InheriterBackup&lt;TransmittableThreadLocal&lt;String&gt;, Void&gt; backup = capture.replay();
 * try {
 *     // 执行需要上下文的业务逻辑
 * } finally {
 *     // 恢复上下文
 *     backup.restore();
 * }
 * </pre>
 *
 * @param <A> 捕获的上下文数据类型
 * @param <B> 备份的上下文数据类型
 * @see Inheriter
 * @see InheriterBackup
 */
public final class InheriterCapture<A, B> extends LinkedHashMap<Inheriter<A, B>, A> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建空的上下文捕获容器，初始容量为16，加载因子为0.75。
     */
    public InheriterCapture() {
        super();
    }

    /**
     * 创建指定初始容量的上下文捕获容器，加载因子为0.75。
     *
     * @param initialCapacity 初始容量，必须为非负数
     */
    public InheriterCapture(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 按注册顺序重放所有捕获的上下文，并返回统一的备份对象。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>创建与当前捕获器大小一致的备份容器</li>
     *   <li>按注册顺序遍历所有Inheriter和捕获的上下文</li>
     *   <li>对每个Inheriter调用replay方法重放上下文</li>
     *   <li>收集replay返回的备份对象到统一容器</li>
     *   <li>返回可用于统一恢复的备份对象</li>
     * </ol>
     *
     * @return 包含所有Inheriter备份的上下文备份容器
     */
    public InheriterBackup<A, B> replay() {
        InheriterBackup<A, B> backup = new InheriterBackup<>(size());
        for (java.util.Map.Entry<Inheriter<A, B>, A> entry : entrySet()) {
            B b = entry.getKey().replay(entry.getValue());
            backup.put(entry.getKey(), b);
        }
        return backup;
    }
}