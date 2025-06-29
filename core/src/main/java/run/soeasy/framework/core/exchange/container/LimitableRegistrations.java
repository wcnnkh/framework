package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;

/**
 * 可组合的注册
 * 支持对注册操作进行限制条件设置和组合操作
 * 
 * @author soeasy.run
 *
 * @param <T> 注册类型
 */
@ToString(callSuper = false)
public class LimitableRegistrations<T extends Registration> extends LimitableRegistration implements Registrations<T> {
    @NonNull
    private final Elements<T> elements;

    /**
     * 基于元素集合创建可限制的注册集合
     * 
     * @param elements 注册元素集合，不可为null
     */
    public LimitableRegistrations(@NonNull Elements<T> elements) {
        this.elements = elements;
    }

    /**
     * 基于现有上下文创建可限制的注册集合
     * 
     * @param context 上下文，不可为null
     */
    protected LimitableRegistrations(LimitableRegistrations<T> context) {
        this.elements = context.elements;
    }

    @Override
    public Elements<T> getElements() {
        return elements;
    }

    @Override
    public boolean isCancellable(BooleanSupplier checker) {
        return super.isCancellable(Registrations.super::isCancellable);
    }

    @Override
    public boolean cancel(BooleanSupplier cancel) {
        return super.cancel(Registrations.super::cancel);
    }

    @Override
    public boolean isCancelled(BooleanSupplier checker) {
        return super.isCancelled(Registrations.super::isCancelled);
    }

    /**
     * 映射转换注册集合元素类型
     * 
     * @param <R> 目标注册类型
     * @param mapper 映射函数，不可为null
     * @return 转换后的可限制注册集合
     */
    public <R extends Registration> LimitableRegistrations<R> map(@NonNull Function<? super T, ? extends R> mapper) {
        return new LimitableRegistrations<>(this.elements.map(mapper));
    }

    /**
     * 组合单个注册
     * 
     * @param registration 待组合的注册，不可为null
     * @return 新的组合后的可限制注册集合
     */
    public LimitableRegistrations<T> combine(@NonNull T registration) {
        return combineAll(Elements.singleton(registration));
    }

    /**
     * 组合多个注册
     * 
     * @param registrations 待组合的注册集合，不可为null
     * @return 新的组合后的可限制注册集合
     */
    public LimitableRegistrations<T> combineAll(@NonNull Elements<? extends T> registrations) {
        return new LimitableRegistrations<>(this.elements.concat(registrations));
    }
}