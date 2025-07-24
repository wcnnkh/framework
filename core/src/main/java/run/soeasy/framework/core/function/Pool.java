package run.soeasy.framework.core.function;

/**
 * 资源池函数式接口，继承自{@link ThrowingSupplier}，扩展了资源关闭方法，
 * 用于定义可提供资源并支持显式关闭的资源池行为，适用于需要管理生命周期的资源（如数据库连接、IO流等）。
 * 
 * <p>该接口结合了资源供应（通过{@link ThrowingSupplier#get()}）和资源关闭（通过{@link #close(Object)}）的能力，
 * 为资源的获取与释放提供统一的函数式契约，便于实现try-with-resources模式或自动资源管理。
 * 
 * @param <T> 资源的类型
 * @param <E> 获取或关闭资源时可能抛出的异常类型
 * @author soeasy.run
 * @see ThrowingSupplier
 */
public interface Pool<T, E extends Throwable> extends ThrowingSupplier<T, E> {

    /**
     * 关闭指定的资源
     * 
     * <p>该方法用于释放由当前资源池提供的资源，确保资源在使用完毕后被正确关闭，避免资源泄露。
     * 
     * @param source 需要关闭的资源（通常是通过{@link #get()}方法获取的实例）
     * @throws E 关闭资源过程中可能抛出的异常
     */
    void close(T source) throws E;
}