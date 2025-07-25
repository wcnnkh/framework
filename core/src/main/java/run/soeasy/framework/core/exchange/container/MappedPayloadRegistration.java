package run.soeasy.framework.core.exchange.container;

import java.util.function.Function;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 映射型有效载荷注册实现，用于将源类型的有效载荷转换为目标类型。
 * <p>
 * 该类实现了{@link PayloadRegistration}和{@link RegistrationWrapper}接口，
 * 允许通过提供的映射函数将源注册的有效载荷从类型{S}转换为类型{T}，
 * 同时保持源注册的其他属性和行为不变。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明转换：通过映射函数实现载荷类型的透明转换</li>
 *   <li>注册包装：保持源注册的所有方法和状态</li>
 *   <li>不可变性：不修改源注册的原始载荷</li>
 * </ul>
 *
 * @param <S> 源注册的有效载荷类型
 * @param <T> 目标注册的有效载荷类型
 * @param <W> 被包装的源注册类型，需继承{@link PayloadRegistration}<{@link S}>
 * 
 * @author soeasy.run
 * @see PayloadRegistration
 * @see RegistrationWrapper
 */
@Data
class MappedPayloadRegistration<S, T, W extends PayloadRegistration<S>>
        implements PayloadRegistration<T>, RegistrationWrapper<W> {
    /** 被包装的源注册对象 */
    @NonNull
    private final W source;
    
    /** 将源载荷映射为目标载荷的函数 */
    @NonNull
    private final Function<? super S, ? extends T> mapper;

    /**
     * 获取映射后的有效载荷
     * <p>
     * 该方法从源注册获取原始载荷，然后通过映射函数转换为目标类型。
     * 
     * @return 映射后的目标类型有效载荷
     */
    @Override
    public T getPayload() {
        S payload = source.getPayload();
        return mapper.apply(payload);
    }
}