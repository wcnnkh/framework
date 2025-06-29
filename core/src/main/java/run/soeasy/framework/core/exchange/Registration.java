package run.soeasy.framework.core.exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 注册操作接口
 * 表示一个已注册的监听器或资源，支持取消注册操作
 * 
 * @author soeasy.run
 */
public interface Registration {
    /** 表示失败状态的Registration实例 */
    static final Registration FAILURE = new Registed(true);
    /** 表示成功状态的Registration实例 */
    static final Registration SUCCESS = new Registed(false);

    /**
     * 将当前Registration与另一个Registration组合
     * 形成一个可以同时取消多个注册的复合Registration
     * 
     * @param registration 要组合的另一个Registration
     * @return 复合Registration实例
     */
    default Registration and(Registration registration) {
        if (registration == null || registration.isCancelled()) {
            return this;
        }

        Elements<Registration> elements = Elements.forArray(this, registration);
        return Registrations.forElements(elements);
    }

    /**
     * 取消注册
     * 
     * @return 如果取消成功返回true，否则返回false
     */
    boolean cancel();

    /**
     * 判断是否可以取消注册
     * 
     * @return 如果可以取消返回true，否则返回false
     */
    boolean isCancellable();

    /**
     * 判断是否已经取消注册
     * 
     * @return 如果已经取消返回true，否则返回false
     */
    boolean isCancelled();

    /**
     * 批量注册并返回一个可批量取消的Registrations对象
     * 若注册过程中发生异常，会自动取消之前已注册的所有项
     * 
     * @param iterable 待注册的元素集合
     * @param register 注册操作的函数式接口
     * @param <E> 注册操作返回的Registration类型
     * @param <S> 待注册的元素类型
     * @param <X> 可能抛出的异常类型
     * @return 包含所有注册项的Registrations对象
     * @throws X 注册过程中可能抛出的异常
     */
    public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
            @NonNull Iterable<? extends S> iterable, ThrowingFunction<? super S, ? extends E, ? extends X> register)
            throws X {
        return registers(iterable.iterator(), register);
    }

    /**
     * 批量注册并返回一个可批量取消的Registrations对象
     * 若注册过程中发生异常，会自动取消之前已注册的所有项
     * 
     * @param iterator 待注册的元素迭代器
     * @param register 注册操作的函数式接口
     * @param <E> 注册操作返回的Registration类型
     * @param <S> 待注册的元素类型
     * @param <X> 可能抛出的异常类型
     * @return 包含所有注册项的Registrations对象
     * @throws X 注册过程中可能抛出的异常
     */
    public static <E extends Registration, S, X extends Throwable> Registrations<E> registers(
            @NonNull Iterator<? extends S> iterator,
            @NonNull ThrowingFunction<? super S, ? extends E, ? extends X> register) throws X {
        List<E> registrations = null;
        while (iterator.hasNext()) {
            S service = iterator.next();
            if (service == null) {
                continue;
            }

            E registration;
            try {
                registration = register.apply(service);
            } catch (Throwable e) {
                if (registrations != null) {
                    try {
                        Collections.reverse(registrations);
                        ThrowingConsumer.acceptAll(registrations.iterator(), (reg) -> reg.cancel());
                    } catch (Throwable e2) {
                        e.addSuppressed(e2);
                    }
                }
                throw e;
            }

            if (registration.isCancelled()) {
                continue;
            }

            if (registrations == null) {
                registrations = new ArrayList<>(8);
            }
            registrations.add(registration);
        }

        Elements<E> elements = Elements.of(registrations);
        return () -> elements;
    }
}