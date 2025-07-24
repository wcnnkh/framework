package run.soeasy.framework.core.type;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 基于构造器的实例工厂，通过指定条件筛选构造器并用于创建对象实例。
 * 该工厂使用提供的构造器工厂获取候选构造器，并通过谓词筛选符合条件的构造器，
 * 适用于需要自定义构造器选择策略的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>构造器筛选：通过谓词动态筛选合适的构造器</li>
 *   <li>灵活配置：支持自定义构造器工厂和筛选条件</li>
 *   <li>实例创建：使用反射通过筛选的构造器创建对象实例</li>
 *   <li>类型安全：基于ResolvableType处理泛型类型信息</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>依赖注入：根据参数类型选择合适的构造器进行依赖注入</li>
 *   <li>对象池：通过无参构造器或特定参数构造器创建池化对象</li>
 *   <li>框架扩展：允许用户自定义构造器选择策略</li>
 *   <li>组件初始化：通过带参构造器进行组件初始化</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建构造器工厂
 * ClassMemberFactory&lt;Constructor&lt;?&gt;&gt; constructorFactory = 
 *     new CachingClassMemberFactory&lt;&gt;(DefaultClassMemberFactory.INSTANCE);
 *     
 * // 创建谓词，筛选无参构造器
 * Predicate&lt;Constructor&lt;?&gt;&gt; noArgPredicate = c -&gt; c.getParameterCount() == 0;
 * 
 * // 创建实例工厂
 * InstanceFactory factory = new ConstructorInstanceFactory(constructorFactory, noArgPredicate);
 * 
 * // 创建实例
 * ResolvableType type = ResolvableType.forClass(User.class);
 * if (factory.canInstantiated(type)) {
 *     User user = (User) factory.newInstance(type);
 * }
 * </pre>
 *
 * @see InstanceFactory
 * @see ClassMemberFactory
 * @see Constructor
 */
@RequiredArgsConstructor
public class ConstructorInstanceFactory implements InstanceFactory {
    /** 用于获取构造器的工厂，不可为null */
    @NonNull
    private final ClassMemberFactory<Constructor<?>> constructorFactory;
    
    /** 用于筛选构造器的谓词，不可为null */
    @NonNull
    private final Predicate<? super Constructor<?>> predicate;

    /**
     * 判断是否可以创建指定类型的实例。
     * <p>
     * 该方法会从构造器工厂获取指定类型的所有构造器，
     * 并使用谓词筛选符合条件的构造器。如果找到符合条件的构造器，
     * 则认为可以创建实例。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 如果可以创建实例返回true，否则返回false
     */
    @Override
    public boolean canInstantiated(@NonNull ResolvableType requiredType) {
        for (Constructor<?> constructor : constructorFactory.getClassMemberProvider(requiredType.getRawType())) {
            if (predicate.test(constructor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建指定类型的实例。
     * <p>
     * 该方法会从构造器工厂获取指定类型的所有构造器，
     * 使用谓词筛选符合条件的构造器，并使用第一个找到的构造器
     * 通过反射创建实例。如果没有找到符合条件的构造器，
     * 则抛出UnsupportedOperationException。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 创建的实例对象
     * @throws UnsupportedOperationException 如果没有找到符合条件的构造器
     */
    @Override
    public Object newInstance(@NonNull ResolvableType requiredType) {
        for (Constructor<?> constructor : constructorFactory.getClassMemberProvider(requiredType.getRawType())) {
            if (predicate.test(constructor)) {
                return ReflectionUtils.newInstance(constructor);
            }
        }
        throw new UnsupportedOperationException("无法为类型 [" + requiredType + "] 找到匹配的构造器");
    }
}