package run.soeasy.framework.core.execute;

/**
 * 方法调用上下文接口，继承自{@link Execution}，扩展了对目标对象的支持，
 * 用于表示对特定对象实例的方法调用，封装了目标对象、方法元数据和执行参数。
 * <p>
 * 该接口是框架中实例方法调用的核心抽象，将方法调用与目标对象绑定，
 * 支持在运行时动态调用对象的方法，并提供参数操作和结果返回的能力。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>目标对象管理：通过{@link #getTarget()}和{@link #setTarget(Object)}管理调用目标</li>
 *   <li>方法元数据：继承{@link Execution}获取完整的方法元数据</li>
 *   <li>参数操作：继承{@link Execution}提供的参数数组操作能力</li>
 *   <li>动态执行：通过{@link #execute()}方法动态执行目标对象的方法</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>拦截器机制：实现AOP拦截，在方法调用前后添加额外逻辑</li>
 *   <li>代理模式：实现方法调用的代理，如RPC远程调用代理</li>
 *   <li>反射调用：通过反射机制动态调用对象方法</li>
 *   <li>命令模式：将方法调用封装为命令对象，支持撤销、记录等操作</li>
 *   <li>框架插件：实现插件方法的动态调用</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Execution
 */
public interface Invocation extends Execution {
    
    /**
     * 获取方法调用的目标对象
     * <p>
     * 对于实例方法，返回该方法所属的对象实例；
     * 对于静态方法，返回该方法所属类的Class对象或null（取决于具体实现）。
     * 
     * @return 方法调用的目标对象
     */
    Object getTarget();

    /**
     * 设置方法调用的目标对象
     * <p>
     * 对于实例方法，应设置为该方法所属的对象实例；
     * 对于静态方法，可设置为null或该方法所属类的Class对象（取决于具体实现）。
     * 
     * @param target 目标对象
     */
    void setTarget(Object target);
}