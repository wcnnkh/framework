package run.soeasy.framework.core.type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 内置支持的实例工厂枚举。
 * 该枚举提供了三种不同的实例化策略，按优先级从高到低排列：
 * <ol>
 *   <li>反射方式：使用无参构造函数创建实例</li>
 *   <li>序列化方式：使用特殊构造器绕过构造函数创建实例</li>
 *   <li>Unsafe方式：使用sun.misc.Unsafe直接分配内存创建实例</li>
 * </ol>
 * 适用于需要根据不同场景选择不同实例化策略的框架或应用。
 *
 * <p>使用场景：
 * <ul>
 *   <li>依赖注入：根据类型选择合适的实例化方式</li>
 *   <li>序列化框架：在反序列化时选择合适的实例化策略</li>
 *   <li>插件系统：动态创建插件实例</li>
 *   <li>测试工具：创建特殊初始化状态的对象</li>
 * </ul>
 *
 * <p>选择策略：
 * <ul>
 *   <li>优先使用REFLECTION：安全且高效，适合大多数情况</li>
 *   <li>其次使用SERIALIZATION：需要绕过构造函数但仍希望执行对象初始化逻辑</li>
 *   <li>最后使用ALLOCATE：需要完全绕过构造函数和初始化逻辑</li>
 * </ul>
 *
 * @see InstanceFactory
 */
@RequiredArgsConstructor
public enum InstanceFactorySupporteds implements InstanceFactory {
    /**
     * 使用反射和无参构造函数创建实例的工厂。
     * 该方式安全且高效，适用于大多数情况。
     * 会尝试查找并使用类的无参构造函数创建实例。
     */
    REFLECTION(
        new ConstructorInstanceFactory(
            new CachingClassMemberFactory<>(ReflectionUtils::getDeclaredConstructors),
            (e) -> e.getParameterCount() == 0
        )
    ),
    
    /**
     * 使用序列化构造器创建实例的工厂。
     * 该方式通过特殊构造器绕过目标类的构造函数，
     * 但仍会执行对象的初始化逻辑（如字段初始化、实例初始化块）。
     */
    SERIALIZATION(
        new ConstructorInstanceFactory(
            new SerializationConstructorFactory(),
            (e) -> true
        )
    ),
    
    /**
     * 使用sun.misc.Unsafe直接分配内存创建实例的工厂。
     * 该方式完全绕过目标类的构造函数和初始化逻辑，
     * 创建的对象处于原始状态，所有字段均为默认值。
     */
    ALLOCATE(
        new AllocateInstanceFactory()
    );

    /** 封装的实例工厂实现 */
    private final InstanceFactory instanceFactory;

    /**
     * 判断当前工厂是否可以创建指定类型的实例。
     * 委托给内部封装的实例工厂实现。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 如果可以创建实例返回true，否则返回false
     */
    @Override
    public boolean canInstantiated(@NonNull ResolvableType requiredType) {
        return instanceFactory.canInstantiated(requiredType);
    }

    /**
     * 使用当前工厂创建指定类型的实例。
     * 委托给内部封装的实例工厂实现。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 创建的实例对象
     * @throws UnsupportedOperationException 如果无法创建实例
     */
    @Override
    public Object newInstance(@NonNull ResolvableType requiredType) {
        return instanceFactory.newInstance(requiredType);
    }
}