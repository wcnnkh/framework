package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.ReceiptWrapper;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 配置化服务包装器接口，用于包装{@link Configured}实例并提供统一的转换与组合能力。
 * <p>
 * 该函数式接口继承自{@link Configured}、{@link IncludeWrapper}和{@link ReceiptWrapper}，
 * 允许对配置化服务实例进行包装转换，同时保持注册生命周期管理、服务提供和操作结果反馈能力，
 * 适用于需要对配置化服务集合进行代理、增强或转换的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>包装适配：将{@link Configured}实例包装为统一接口，支持透明代理</li>
 *   <li>流式转换：通过{@link #map}方法实现服务实例流的转换与映射</li>
 *   <li>注册组合：通过{@link #and}方法组合多个注册操作的生命周期管理</li>
 *   <li>结果反馈：继承{@link ReceiptWrapper}，提供操作结果的透明访问</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * @param <W> 被包装的{@link Configured}实现类型
 * 
 * @author soeasy.run
 * @see Configured
 * @see IncludeWrapper
 * @see ReceiptWrapper
 */
@FunctionalInterface
public interface ConfiguredWrapper<S, W extends Configured<S>>
        extends Configured<S>, IncludeWrapper<S, W>, ReceiptWrapper<W> {

    /**
     * 转换服务实例流并返回新的Configured实例
     * <p>
     * 委托给被包装的源{@link Configured}实例执行流式转换，
     * 支持通过resize参数控制结果集大小调整策略。
     * 
     * @param <U>         转换后的服务实例类型
     * @param resize      是否按转换结果调整集合大小
     * @param converter   流式转换函数，不可为null
     * @return 转换后的Configured实例
     * @throws NullPointerException 若converter为null
     */
    @Override
    default <U> Configured<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
        return getSource().map(resize, converter);
    }

    /**
     * 组合注册操作并返回新的Configured实例
     * <p>
     * 委托给被包装的源{@link Configured}实例执行注册组合，
     * 新实例将同时管理当前注册和传入注册的生命周期。
     * 
     * @param registration 要组合的注册操作，不可为null
     * @return 组合后的Configured实例
     * @throws NullPointerException 若registration为null
     */
    @Override
    default Configured<S> and(Registration registration) {
        return getSource().and(registration);
    }
}