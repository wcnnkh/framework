package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ConvertedProvider;

/**
 * 转换服务包含实现类，将源类型服务包含转换为目标类型，实现{@link Include}接口。
 * <p>
 * 该类继承自{@link ConvertedProvider}，在转换服务提供者的基础上，
 * 提供服务包含的生命周期管理能力，适用于需要动态转换服务类型的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型转换：通过函数式接口将{@link Stream<S>}转换为{@link Stream<T>}</li>
 *   <li>生命周期透传：取消操作、可取消状态等直接透传给目标包含实例</li>
 *   <li>链式转换：支持通过{@link #map}方法进行多层级类型转换</li>
 * </ul>
 *
 * @param <S> 源服务实例类型
 * @param <T> 目标服务实例类型
 * @param <W> 源包含实现类型（需实现{@link Include<S>}）
 * 
 * @author soeasy.run
 * @see ConvertedProvider
 * @see Include
 */
class ConvertedInclude<S, T, W extends Include<S>> extends ConvertedProvider<S, T, W> implements Include<T> {

    /**
     * 构造函数，创建转换服务包含实例
     * 
     * @param target    源包含实例，不可为null
     * @param resize    是否调整结果大小（true表示按转换结果调整，false保持原大小）
     * @param converter 流式转换函数，不可为null
     * @throws NullPointerException 若target或converter为null
     */
    public ConvertedInclude(@NonNull W target, boolean resize,
            @NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
        super(target, resize, converter);
    }

    /**
     * 取消包含的服务注册
     * <p>
     * 透传给目标包含实例的取消操作，返回目标的取消结果
     * 
     * @return 取消是否成功
     */
    @Override
    public boolean cancel() {
        return getTarget().cancel();
    }

    /**
     * 判断是否可取消
     * <p>
     * 透传给目标包含实例的可取消状态查询
     * 
     * @return 是否可取消
     */
    @Override
    public boolean isCancellable() {
        return getTarget().isCancellable();
    }

    /**
     * 判断是否已取消
     * <p>
     * 透传给目标包含实例的取消状态查询
     * 
     * @return 是否已取消
     */
    @Override
    public boolean isCancelled() {
        return getTarget().isCancelled();
    }

    /**
     * 转换服务实例流并返回新的Include实例
     * <p>
     * 调用{@link Include}接口的默认实现，支持链式类型转换
     * 
     * @param <U>         转换后的服务实例类型
     * @param resize      是否调整结果大小
     * @param converter   流式转换函数
     * @return 转换后的Include实例
     */
    @Override
    public <U> Include<U> map(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
        return Include.super.map(resize, converter);
    }
}