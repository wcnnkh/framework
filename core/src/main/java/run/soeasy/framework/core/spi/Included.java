package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.collection.ProviderWrapper;
import run.soeasy.framework.core.exchange.Receipted;

/**
 * 服务包含实现类，包装服务提供者并提供配置结果反馈，实现{@link Configured}接口。
 * <p>
 * 该类继承自{@link Receipted}，用于封装服务提供者{@link Provider<S>}，
 * 同时提供操作结果状态（成功/失败）和流式转换能力，适用于服务配置流程的结果封装。
 * </p>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see Configured
 * @see ProviderWrapper
 * @see Receipted
 */
class Included<S> extends Receipted implements Configured<S>, ProviderWrapper<S, Provider<S>> {
    private static final long serialVersionUID = 1L;
    
    /** 表示配置失败的Configured实例，用于统一错误处理 */
    public static final Included<?> FAILURE_CONFIGURED = new Included<>(true, false, null);
    
    /** 被包装的服务提供者，用于获取服务实例 */
    private final Provider<S> source;

    /**
     * 构造函数，创建配置结果为指定状态的服务包含实例
     * 
     * @param done    操作是否完成（true表示已完成，false表示进行中）
     * @param success 操作是否成功（true表示成功，false表示失败）
     * @param cause   操作失败的原因（成功时为null）
     */
    public Included(boolean done, boolean success, Throwable cause) {
        this(done, success, cause, Provider.empty());
    }

    /**
     * 构造函数，创建配置结果为指定状态的服务包含实例
     * 
     * @param done    操作是否完成（true表示已完成，false表示进行中）
     * @param success 操作是否成功（true表示成功，false表示失败）
     * @param cause   操作失败的原因（成功时为null）
     * @param source  被包装的服务提供者，不可为null
     */
    public Included(boolean done, boolean success, Throwable cause, Provider<S> source) {
        super(done, success, cause);
        this.source = source;
    }

    /**
     * 获取被包装的服务提供者
     * 
     * @return 服务提供者实例
     */
    @Override
    public Provider<S> getSource() {
        return source;
    }

    /**
     * 转换服务实例流并返回新的Configured实例
     * <p>
     * 调用{@link Configured#map}接口默认实现，执行流式转换
     * 
     * @param <U>         转换后的服务实例类型
     * @param resize      是否调整结果大小
     * @param converter   流式转换函数，不可为null
     * @return 转换后的Configured实例
     * @throws NullPointerException 若converter为null
     */
    @Override
    public <U> Configured<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return Configured.super.map(resize, converter);
    }
}