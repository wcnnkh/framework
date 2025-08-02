package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 服务包含接口，集成注册管理与服务提供能力，支持服务实例的转换与组合操作。
 * <p>
 * 该接口继承自{@link Registration}和{@link Provider}，既可以管理注册生命周期，
 * 又能提供服务实例的访问能力，适用于需要动态管理服务集合的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注册管理：继承{@link Registration}，支持取消注册操作</li>
 *   <li>服务提供：继承{@link Provider}，支持服务实例的迭代访问</li>
 *   <li>流式转换：通过{@link #map}方法支持服务实例的流式转换</li>
 *   <li>注册组合：通过{@link #and}方法支持多个注册的组合管理</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see Registration
 * @see Provider
 */
public interface Include<S> extends Registration, Provider<S> {

    /**
     * 转换服务实例流，返回新的Include实例
     * <p>
     * 通过流式转换函数对服务实例进行转换，并根据resize参数决定是否调整结果大小。
     * 该操作会创建一个新的{@link ConvertedInclude}实例，包装原Include的功能。
     * 
     * @param <U>         转换后的服务实例类型
     * @param resize      是否调整结果大小（true表示按转换结果调整，false表示保持原大小）
     * @param converter   流式转换函数，不可为null
     * @return 转换后的Include实例
     * @throws NullPointerException 若converter为null
     */
    @Override
    default <U> Include<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return new ConvertedInclude<>(this, resize, converter);
    }

    /**
     * 组合多个注册操作，返回新的Include实例
     * <p>
     * 将当前Include与另一个注册操作组合，新实例的取消操作会同时取消两者。
     * 该操作会创建一个新的{@link AndInclude}实例，包装多个注册的功能。
     * 
     * @param registration 要组合的注册操作，不可为null
     * @return 组合后的Include实例
     * @throws NullPointerException 若registration为null
     */
    @Override
    default Include<S> and(Registration registration) {
        return new AndInclude<>(this, registration);
    }
}