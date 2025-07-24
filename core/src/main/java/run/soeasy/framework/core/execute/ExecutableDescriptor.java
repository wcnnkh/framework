package run.soeasy.framework.core.execute;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 可执行元素描述符接口，继承自{@link SourceDescriptor}和{@link AnnotatedElement}，
 * 用于描述可执行元素（如方法、构造函数）的元信息和执行条件。
 * <p>
 * 该接口提供了判断可执行元素是否可以被执行的能力，支持无参和带参两种判断方式。
 * 实现类通常封装了反射API中的Method或Constructor，并提供更友好的访问接口。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>执行条件判断：通过{@link #canExecuted(Class[])}方法判断可执行元素是否可以被调用</li>
 *   <li>注解支持：继承自AnnotatedElement，可获取可执行元素上的注解信息</li>
 *   <li>源描述符：继承自SourceDescriptor，可获取可执行元素的来源信息</li>
 *   <li>参数类型匹配：提供基于参数类型的执行条件判断</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>框架反射调用：在运行时根据参数类型选择合适的方法或构造函数</li>
 *   <li>依赖注入：判断构造函数或方法是否满足注入条件</li>
 *   <li>插件系统：动态加载并执行符合条件的插件方法</li>
 *   <li>命令模式实现：根据命令参数选择合适的执行方法</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see SourceDescriptor
 * @see AnnotatedElement
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 */
public interface ExecutableDescriptor extends SourceDescriptor, AnnotatedElement {
    
    /**
     * 判断可执行元素是否可以无参执行
     * <p>
     * 该默认实现调用{@link #canExecuted(Class[])}方法，
     * 传入空参数类型数组，用于判断可执行元素是否可以无参调用。
     * </p>
     * 
     * @return 若可执行元素可以无参执行返回true，否则返回false
     */
    default boolean canExecuted() {
        return canExecuted(ClassUtils.emptyArray());
    }

    /**
     * 判断可执行元素是否可以使用指定参数类型执行
     * <p>
     * 该方法用于检查可执行元素是否可以接受给定类型的参数，
     * 通常通过比较参数类型的兼容性来实现。
     * </p>
     * 
     * @param parameterTypes 参数类型数组，不可为null
     * @return 若可执行元素可以接受指定参数类型返回true，否则返回false
     */
    boolean canExecuted(@NonNull Class<?>... parameterTypes);
}