package run.soeasy.framework.core.type;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 组合式实例工厂，用于将多个实例工厂组合为一个统一的工厂。
 * 该工厂会按顺序遍历内部的实例工厂列表，找到第一个能够创建指定类型实例的工厂，
 * 并委托其创建实例，适用于需要根据不同条件选择不同工厂的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>工厂组合：将多个实例工厂组合为一个统一接口</li>
 *   <li>顺序遍历：按注册顺序遍历工厂，直到找到合适的工厂</li>
 *   <li>短路机制：找到第一个匹配的工厂后立即返回，不再继续遍历</li>
 *   <li>不可变性：工厂列表在创建后不可修改，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>插件系统：根据插件类型选择对应的工厂创建实例</li>
 *   <li>多策略支持：根据不同条件选择不同的实例化策略</li>
 *   <li>框架扩展：允许用户通过注册自定义工厂扩展框架功能</li>
 *   <li>依赖注入：根据类型选择合适的依赖注入方式</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建两个不同的实例工厂
 * InstanceFactory stringFactory = new StringInstanceFactory();
 * InstanceFactory numberFactory = new NumberInstanceFactory();
 * 
 * // 组合为一个工厂
 * InstanceFactory combinedFactory = new MultiableInstanceFactory(stringFactory, numberFactory);
 * 
 * // 创建实例
 * ResolvableType stringType = ResolvableType.forClass(String.class);
 * if (combinedFactory.canInstantiated(stringType)) {
 *     String instance = (String) combinedFactory.newInstance(stringType);
 *     System.out.println("创建的字符串实例: " + instance);
 * }
 * </pre>
 *
 * @see InstanceFactory
 */
@RequiredArgsConstructor
@Getter
public class MultiableInstanceFactory implements InstanceFactory {
    /** 内部维护的实例工厂列表，不可为null */
    @NonNull
    private final Iterable<? extends InstanceFactory> iterable;

    /**
     * 使用多个实例工厂创建组合式工厂。
     * <p>
     * 该构造函数会将传入的多个工厂按顺序添加到内部列表中。
     *
     * @param instanceFactories 实例工厂数组，不可为null
     */
    public MultiableInstanceFactory(@NonNull InstanceFactory... instanceFactories) {
        this(Arrays.asList(instanceFactories));
    }

    /**
     * 判断组合工厂中是否有任何一个工厂能够创建指定类型的实例。
     * <p>
     * 该方法会按顺序遍历内部工厂列表，直到找到第一个能够创建实例的工厂。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 如果有任何一个工厂能够创建实例返回true，否则返回false
     */
    @Override
    public boolean canInstantiated(@NonNull ResolvableType requiredType) {
        for (InstanceFactory instanceFactory : iterable) {
            if (instanceFactory.canInstantiated(requiredType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建指定类型的实例。
     * <p>
     * 该方法会按顺序遍历内部工厂列表，找到第一个能够创建实例的工厂并委托其创建。
     * 如果所有工厂都无法创建实例，则抛出UnsupportedOperationException。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 创建的实例对象
     * @throws UnsupportedOperationException 如果所有工厂都无法创建实例
     */
    @Override
    public Object newInstance(@NonNull ResolvableType requiredType) {
        for (InstanceFactory instanceFactory : iterable) {
            if (instanceFactory.canInstantiated(requiredType)) {
                return instanceFactory.newInstance(requiredType);
            }
        }
        throw new UnsupportedOperationException("不支持创建类型: " + requiredType);
    }
}