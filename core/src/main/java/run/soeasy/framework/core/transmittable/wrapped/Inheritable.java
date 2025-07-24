package run.soeasy.framework.core.transmittable.wrapped;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.transmittable.Inheriter;

/**
 * 支持上下文传递的可继承对象包装器。 该类将任意对象与{@link Inheriter}组合，使其具备上下文传递能力，
 * 实现跨线程或跨执行阶段的上下文信息传递，同时保持被包装对象的原有功能。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>上下文感知：通过关联的Inheriter实现上下文的捕获、传递和恢复</li>
 * <li>非侵入性：不修改原始对象结构，通过包装模式添加上下文传递能力</li>
 * <li>类型安全：使用泛型确保上下文类型与操作的一致性</li>
 * <li>灵活适配：支持任意类型的对象和自定义Inheriter实现</li>
 * </ul>
 *
 * <p>
 * 典型使用模式：
 * 
 * <pre class="code">
 * // 创建可继承对象
 * Inheritable&lt;Context, Backup, ContextInheriter, MyService&gt; inheritable = new Inheritable&lt;&gt;(myService,
 * 		contextInheriter);
 * 
 * // 在执行前后进行上下文管理
 * Backup backup = inheritable.inheriter().replay(inheritable.inheriter().capture());
 * try {
 * 	// 执行需要上下文的操作
 * 	inheritable.getSource().doSomething();
 * } finally {
 * 	inheritable.inheriter().restore(backup);
 * }
 * </pre>
 *
 * <p>
 * 设计意图：
 * <ul>
 * <li>解耦上下文管理与业务逻辑，保持代码的单一职责</li>
 * <li>提供统一的上下文传递接口，简化复杂环境下的上下文管理</li>
 * <li>支持在不修改原有代码的情况下，为现有系统添加上下文传递能力</li>
 * </ul>
 *
 * @param <A> 上下文捕获的数据类型
 * @param <B> 上下文备份的数据类型
 * @param <I> 继承器的具体类型，必须实现Inheriter接口
 * @param <W> 被包装的原始对象类型
 * 
 * @see Wrapped
 * @see Inheriter
 */
@Getter
public class Inheritable<A, B, I extends Inheriter<A, B>, W> extends Wrapped<W> {
	/** 关联的上下文继承器，用于管理上下文传递 */
	@NonNull
	private final I inheriter;

	/**
	 * 创建支持上下文传递的可继承对象。
	 * 
	 * @param source    被包装的原始对象，不可为null
	 * @param inheriter 用于管理上下文的继承器，不可为null
	 */
	public Inheritable(@NonNull W source, @NonNull I inheriter) {
		super(source);
		this.inheriter = inheriter;
	}
}