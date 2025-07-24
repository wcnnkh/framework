package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyDescriptor;

/**
 * 可执行元素参数描述符，实现{@link PropertyDescriptor}接口，
 * 用于封装Java反射中的{@link Parameter}对象，提供参数的元数据描述和属性级访问能力。
 * <p>
 * 该类将反射参数转换为属性描述符，支持参数类型、名称等元数据的延迟加载和类型描述符转换，
 * 适用于参数绑定、属性映射等需要参数元数据的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Parameter对象转换为属性描述符</li>
 *   <li>延迟加载：参数类型描述符采用懒加载机制，首次访问时才进行转换</li>
 *   <li>线程安全：使用双重检查锁实现类型描述符的线程安全初始化</li>
 *   <li>类型增强：将反射参数转换为{@link TypeDescriptor}，支持泛型和注解元数据</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数绑定：将请求参数映射到方法参数（如Web框架、RPC框架）</li>
 *   <li>属性映射：在不同对象的参数间进行值映射</li>
 *   <li>动态代理：生成包含参数元数据的代理方法</li>
 *   <li>参数校验：基于参数类型和名称实现参数校验逻辑</li>
 *   <li>日志记录：记录方法调用时的参数名称和类型</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Parameter
 * @see PropertyDescriptor
 * @see TypeDescriptor
 */
@Data
@EqualsAndHashCode(of = "parameter")
@ToString(of = "parameter")
public class ExecutableParameterDescriptor implements PropertyDescriptor {
    /**
     * 封装的反射Parameter对象
     */
    @NonNull
    private final Parameter parameter;
    
    /**
     * 缓存的参数类型描述符，延迟初始化
     */
    private volatile TypeDescriptor typeDescriptor;

    /**
     * 获取参数的类型描述符（延迟初始化）
     * <p>
     * 该方法使用双重检查锁机制延迟初始化类型描述符：
     * <ol>
     *   <li>首次调用时通过{@link TypeDescriptor#forParameter(Parameter)}转换参数类型</li>
     *   <li>使用volatile和同步块确保多线程环境下的安全初始化</li>
     *   <li>类型描述符创建后会被缓存，后续调用直接返回缓存结果</li>
     * </ol>
     * </p>
     * 
     * @return 参数的类型描述符
     */
    public TypeDescriptor getTypeDescriptor() {
        if (typeDescriptor == null) {
            synchronized (this) {
                if (typeDescriptor == null) {
                    typeDescriptor = TypeDescriptor.forParameter(parameter);
                }
            }
        }
        return typeDescriptor;
    }

    /**
     * 获取参数名称
     * <p>
     * 直接返回反射Parameter对象的名称，
     * 该名称的可用性依赖于编译时是否使用{@code -parameters}标志
     * </p>
     * 
     * @return 参数名称
     * @see Parameter#getName()
     */
    @Override
    public String getName() {
        return parameter.getName();
    }

    /**
     * 获取参数的返回类型描述符（与参数类型相同）
     * <p>
     * 实现{@link PropertyDescriptor}接口，返回参数的类型描述符
     * </p>
     * 
     * @return 参数的类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 获取参数所需的类型描述符（与参数类型相同）
     * <p>
     * 实现{@link PropertyDescriptor}接口，返回参数的类型描述符
     * </p>
     * 
     * @return 参数的类型描述符
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        return getTypeDescriptor();
    }
}