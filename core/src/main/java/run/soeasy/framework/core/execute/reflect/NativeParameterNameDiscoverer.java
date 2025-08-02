package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import lombok.NonNull;

/**
 * 原生参数名称发现器，实现{@link ParameterNameDiscoverer}接口，
 * 基于Java 8+反射API的{@link Parameter#getName()}方法获取可执行元素的参数名称。
 * <p>
 * 该实现直接通过{@link Executable#getParameters()}获取参数数组，
 * 并检查每个参数的名称可用性（{@link Parameter#isNamePresent()}），
 * 仅在所有参数名称都可获取时返回名称数组，否则返回null。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>JDK原生支持：依赖Java 8+的Parameter.getName()特性</li>
 *   <li>全或无策略：仅当所有参数名称都可获取时才返回结果</li>
 *   <li>零依赖实现：无需额外字节码工具或库支持</li>
 *   <li>性能高效：直接调用JDK原生方法，无额外开销</li>
 * </ul>
 *
 * <p><b>使用限制：</b>
 * <ul>
 *   <li>JDK版本要求：必须运行在Java 8或更高版本</li>
 *   <li>编译参数要求：类必须使用{@code -parameters}标志编译以保留参数名称</li>
 *   <li>兼容性限制：无法获取匿名类、Lambda表达式的参数名称</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>现代Java项目（JDK 8+）的参数名称解析</li>
 *   <li>无需兼容旧JDK版本的参数绑定场景</li>
 *   <li>对依赖无要求的轻量级参数名称发现</li>
 *   <li>性能敏感型场景（如高频反射调用）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ParameterNameDiscoverer
 * @see java.lang.reflect.Parameter
 */
class NativeParameterNameDiscoverer implements ParameterNameDiscoverer {

    /**
     * 通过Java原生反射获取可执行元素的参数名称
     * <p>
     * 该方法执行流程：
     * <ol>
     *   <li>获取可执行元素的参数数组</li>
     *   <li>检查每个参数的名称可用性</li>
     *   <li>若所有参数名称都可用则收集返回，否则返回null</li>
     * </ol>
     * 注意：参数名称的可用性依赖于编译时是否使用{@code -parameters}标志
     * 
     * @param executable 可执行元素（Method/Constructor），不可为null
     * @return 参数字符串数组，不可用时返回null
     * @see Parameter#isNamePresent()
     * @see Parameter#getName()
     */
    @Override
    public String[] getParameterNames(@NonNull Executable executable) {
        Parameter[] parameters = executable.getParameters();
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames[i] = param.getName();
        }
        return parameterNames;
    }
}