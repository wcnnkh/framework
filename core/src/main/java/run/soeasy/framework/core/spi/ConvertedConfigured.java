package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * 配置化服务转换包装类，将源类型配置化服务转换为目标类型，实现{@link Configured}接口。
 * <p>
 * 该类继承自{@link ConvertedInclude}，在转换服务包含的基础上，
 * 提供配置结果状态的透传能力，适用于需要动态转换服务配置类型的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型转换：通过函数式接口将{@link Stream<S>}转换为{@link Stream<T>}</li>
 *   <li>状态透传：配置结果状态（完成、成功、失败原因）直接透传给目标配置实例</li>
 *   <li>链式转换：支持通过{@link #map}方法进行多层级配置类型转换</li>
 * </ul>
 *
 * @param <S> 源服务配置实例类型
 * @param <T> 目标服务配置实例类型
 * @param <W> 源配置实现类型（需实现{@link Configured<S>}）
 * 
 * @author soeasy.run
 * @see ConvertedInclude
 * @see Configured
 */
class ConvertedConfigured<S, T, W extends Configured<S>> extends ConvertedInclude<S, T, W>
        implements Configured<T> {

    /**
     * 构造函数，创建配置化服务转换实例
     * 
     * @param target    源配置实例，不可为null
     * @param resize    是否调整结果大小（true表示按转换结果调整，false保持原大小）
     * @param converter 流式转换函数，不可为null
     * @throws NullPointerException 若target或converter为null
     */
    public ConvertedConfigured(@NonNull W target, boolean resize,
            @NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
        super(target, resize, converter);
    }

    /**
     * 转换配置服务实例流并返回新的Configured实例
     * <p>
     * 调用{@link Configured}接口的默认实现，支持链式配置类型转换
     * 
     * @param <U>         转换后的配置服务实例类型
     * @param resize      是否调整结果大小
     * @param converter   流式转换函数
     * @return 转换后的Configured实例
     */
    @Override
    public <U> Configured<U> map(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
        return Configured.super.map(resize, converter);
    }

    /**
     * 获取配置操作的失败原因
     * <p>
     * 透传给目标配置实例的失败原因查询
     * 
     * @return 失败原因（成功时为null）
     */
    @Override
    public Throwable cause() {
        return getTarget().cause();
    }

    /**
     * 判断配置操作是否完成
     * <p>
     * 透传给目标配置实例的完成状态查询
     * 
     * @return true表示已完成，false表示未完成
     */
    @Override
    public boolean isDone() {
        return getTarget().isDone();
    }

    /**
     * 判断配置操作是否成功
     * <p>
     * 透传给目标配置实例的成功状态查询
     * 
     * @return true表示成功，false表示失败
     */
    @Override
    public boolean isSuccess() {
        return getTarget().isSuccess();
    }
}