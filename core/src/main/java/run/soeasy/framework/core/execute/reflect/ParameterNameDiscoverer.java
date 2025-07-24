package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;

import lombok.NonNull;

/**
 * 参数名称发现器接口，用于获取可执行元素（方法或构造函数）的参数名称，
 * 支持通过反射或其他元数据机制解析参数名称，为参数绑定、日志记录等场景提供参数名信息。
 * <p>
 * 该接口是框架中参数元数据解析的核心抽象，不同实现可支持不同的参数名称获取策略，
 * 如基于Java 8+的Parameter.getName()、调试信息中的局部变量表、字节码增强等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>可执行元素支持：适用于Method和Constructor等Executable实现</li>
 *   <li>灵活解析策略：支持多种参数名称解析方式（反射、字节码、注解等）</li>
 *   <li>容错处理：无法解析时返回null，数组元素可能包含null</li>
 *   <li>性能优化：支持缓存解析结果，避免重复解析</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>参数绑定：将请求参数映射到方法参数（如Web框架、RPC框架）</li>
 *   <li>日志记录：记录方法调用时的参数名称和值</li>
 *   <li>动态代理：生成包含参数名称的代理方法</li>
 *   <li>Bean映射：基于参数名称实现对象属性映射</li>
 *   <li>调试工具：生成包含参数名称的调试信息</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Executable
 * @see java.lang.reflect.Parameter
 */
public interface ParameterNameDiscoverer {

    /**
     * 获取可执行元素的参数名称数组
     * <p>
     * 该方法尝试解析指定可执行元素的参数名称，返回的数组长度与参数数量一致。
     * 若无法解析参数名称（如无调试信息或JVM不支持），则返回null。
     * 数组中个别元素可能为null（如仅部分参数可解析时），但推荐使用占位符名称。
     * </p>
     * 
     * @param executable 可执行元素（Method或Constructor），不可为null
     * @return 参数名称数组，无法解析时返回null
     * @see java.lang.reflect.Parameter#getName()
     * @see org.springframework.core.LocalVariableTableParameterNameDiscoverer
     */
    String[] getParameterNames(@NonNull Executable executable);
}