package run.soeasy.framework.core.transmittable.wrapped;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.transmittable.Inheriter;
import run.soeasy.framework.core.transmittable.InheriterBackup;

/**
 * 已捕获上下文状态的可继承对象包装器。
 * 该类继承自{@link Inheritable}，在对象创建时自动捕获当前上下文状态，
 * 并通过Lombok的{@code @Getter}注解提供不可变访问，确保上下文状态的一致性传递。
 *
 * <p>核心特性：
 * <ul>
 *   <li>自动上下文捕获：实例化时立即调用{@link Inheriter#capture()}捕获上下文</li>
 *   <li>不可变状态保存：使用{@code final}字段存储捕获的上下文，保证线程安全</li>
 *   <li>简化访问接口：通过Lombok注解自动生成类型安全的访问器方法</li>
 *   <li>无缝集成框架：与框架中其他上下文传递组件保持设计一致性</li>
 * </ul>
 *
 * <p>典型使用模式：
 * <pre class="code">
 * // 创建并捕获上下文
 * Captured&lt;ContextType, BackupType, MyInheriter, MyService&gt; captured = 
 *     new Captured&lt;&gt;(myService, contextInheriter);
 * 
 * // 获取捕获的上下文状态
 * ContextType context = captured.getCapture();
 * 
 * // 结合其他组件执行上下文传递
 * InheriterBackup&lt;ContextType, BackupType&gt; backup = captured.inheriter().replay(context);
 * try {
 *     // 在重放的上下文环境中执行操作
 *     captured.getSource().doBusiness();
 * } finally {
 *     // 恢复原始上下文状态
 *     backup.restore();
 * }
 * </pre>
 *
 * <p>设计考量：
 * <ul>
 *   <li>线程安全：捕获操作发生在构造阶段，避免多线程环境下的竞态条件</li>
 *   <li>代码简洁：通过Lombok减少样板代码，提高可维护性</li>
 *   <li>扩展性：继承自Inheritable，保留父类的所有功能</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <W> 被包装的原始对象类型
 * 
 * @see Inheritable
 * @see Inheriter
 * @see InheriterBackup
 */
@Getter
public class Captured<A, B, I extends Inheriter<A, B>, W> extends Inheritable<A, B, I, W> {
    /**
     * 创建对象时捕获的上下文状态，不可变且线程安全。
     * 该字段通过Lombok的{@code @Getter}注解自动生成访问器方法。
     */
    private final A capture;

    /**
     * 创建已捕获上下文的可继承对象。
     * 在对象初始化过程中立即调用传入的Inheriter捕获当前上下文状态，
     * 确保后续操作使用一致的上下文环境。
     * 
     * @param source 被包装的原始对象，不可为null
     * @param inheriter 用于管理上下文的继承器，不可为null
     */
    public Captured(@NonNull W source, I inheriter) {
        super(source, inheriter);
        // 关键操作：在对象创建时立即捕获上下文状态
        this.capture = inheriter.capture();
    }
}