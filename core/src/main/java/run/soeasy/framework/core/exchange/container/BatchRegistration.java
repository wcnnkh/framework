package run.soeasy.framework.core.exchange.container;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 批量注册管理类，用于组合和管理多个可限制注册对象，支持批量操作和行为拦截。
 * <p>
 * 该类继承自{@link InterceptableRegisration}，提供了批量注册的组合、拦截和批量处理能力，
 * 允许通过函数式接口定义注册组合逻辑和批量操作行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>批量注册组合：支持将多个注册对象组合为一个批量注册单元</li>
 *   <li>行为拦截：通过{@link InterceptableRegisration}实现注册行为的前置和后置拦截</li>
 *   <li>函数式组合：使用{@link BiFunction}定义注册对象的组合逻辑</li>
 *   <li>批量操作：支持对组合的注册对象执行批量映射和处理</li>
 * </ul>
 *
 * @param <E> 注册对象类型，需继承{@link LimitableRegistration}
 * 
 * @author soeasy.run
 * @see InterceptableRegisration
 * @see LimitableRegistration
 */
public class BatchRegistration<E extends LimitableRegistration>
        extends InterceptableRegisration<Registration, E, Registration> {
    
    /** 注册组合函数，用于将注册对象与其他注册组合 */
    @NonNull
    private final BiFunction<? super E, ? super Registration, ? extends E> andFunction;

    /**
     * 构造函数，基于现有上下文和组合函数创建批量注册
     * 
     * @param context 拦截注册上下文，不可为null
     * @param andFunction 注册组合函数，不可为null
     * @throws NullPointerException 若context或andFunction为null
     */
    private BatchRegistration(@NonNull InterceptableRegisration<Registration, E, Registration> context,
            @NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
        super(context);
        this.andFunction = andFunction;
    }

    /**
     * 构造函数，基于注册集合和组合函数创建批量注册
     * 
     * @param registrations 注册集合，不可为null
     * @param andFunction 注册组合函数，不可为null
     * @throws NullPointerException 若registrations或andFunction为null
     */
    public BatchRegistration(@NonNull Elements<E> registrations,
            @NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
        super(registrations);
        this.andFunction = andFunction;
    }

    /**
     * 复制构造函数，用于创建批量注册的副本
     * 
     * @param batchRegistration 要复制的批量注册对象
     */
    protected BatchRegistration(BatchRegistration<E> batchRegistration) {
        this(batchRegistration, batchRegistration.andFunction);
    }

    /**
     * 组合单个注册对象到当前批量注册
     * 
     * @param registration 要组合的注册对象，不可为null
     * @return 新的批量注册实例，包含原注册和新注册
     * @throws NullPointerException 若registration为null
     */
    @Override
    public BatchRegistration<E> combine(@NonNull E registration) {
        return new BatchRegistration<>(super.combine(registration), this.andFunction);
    }

    /**
     * 组合多个注册对象到当前批量注册
     * 
     * @param registrations 要组合的注册集合，不可为null
     * @return 新的批量注册实例，包含原注册和新注册集合
     * @throws NullPointerException 若registrations为null
     */
    @Override
    public BatchRegistration<E> combineAll(@NonNull Elements<? extends E> registrations) {
        return new BatchRegistration<>(super.combineAll(registrations), this.andFunction);
    }

    /**
     * 对批量注册应用自定义批量处理逻辑
     * <p>
     * 该方法会为每个注册对象应用批量映射函数，并将结果组合到新的批量注册中。
     * 同时会在取消时执行批量映射函数的取消逻辑。
     * 
     * @param batchMapper 批量映射函数，不可为null
     * @return 应用批量处理后的新批量注册实例
     * @throws NullPointerException 若batchMapper为null
     */
    public BatchRegistration<E> batch(@NonNull Function<? super Elements<E>, ? extends Registration> batchMapper) {
        Registration batch = new DisposableRegistration(() -> {
            // 限制各元素的行为
            Elements<E> source = getElements().filter((e) -> e.getLimiter().limited());
            Registration registration = batchMapper.apply(source);
            // 执行批量行为
            registration.cancel();
        });

        return new BatchRegistration<>(super.pre(batch).map((r) -> {
            // 为每个元素添加行为
            Registration registration = batchMapper.apply(Elements.singleton(r));
            return andFunction.apply(r, registration);
        }), this.andFunction);
    }
}