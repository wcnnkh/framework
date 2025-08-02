package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 可执行元素异常类型描述符提供者，实现{@link Provider}接口，
 * 用于封装可执行元素（Method/Constructor）的异常类型元数据，提供类型描述符的懒加载和迭代访问能力。
 * <p>
 * 该类通过反射获取可执行元素声明的异常类型，并将其转换为{@link TypeDescriptor}体系，
 * 支持泛型类型解析、注解元数据提取，适用于异常处理、类型校验等需要异常类型信息的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>懒加载机制：首次访问时才通过反射获取异常类型，避免不必要的性能开销</li>
 *   <li>线程安全：使用双重检查锁实现线程安全的元数据加载和缓存</li>
 *   <li>类型增强：将反射类型转换为{@link TypeDescriptor}，支持泛型和注解元数据</li>
 *   <li>缓存策略：加载后的类型描述符会被缓存，多次访问无需重复反射</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>异常处理：在反射调用时获取可执行元素声明的异常类型</li>
 *   <li>类型校验：验证方法调用可能抛出的异常类型</li>
 *   <li>框架插件：为AOP切面提供异常类型元数据</li>
 *   <li>动态代理：生成包含异常声明的代理方法</li>
 *   <li>API文档：提取方法异常信息生成文档</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Executable
 * @see TypeDescriptor
 * @see Provider
 */
@RequiredArgsConstructor
@ToString(of = "executable")
@EqualsAndHashCode(of = "executable")
public class ExecutableExceptionTypeDescriptors implements Provider<TypeDescriptor> {
    /**
     * 封装的可执行元素（Method/Constructor）
     */
    @NonNull
    @Getter
    private final Executable executable;
    
    /**
     * 缓存的异常类型描述符数组，延迟初始化
     */
    private volatile TypeDescriptor[] typeDescriptors;

    /**
     * 获取异常类型描述符的迭代器
     * <p>
     * 首次调用时触发异常类型的加载和转换，后续调用直接返回缓存结果
     * 
     * @return 异常类型描述符迭代器
     */
    @Override
    public Iterator<TypeDescriptor> iterator() {
        reload(false);
        return Arrays.asList(typeDescriptors).iterator();
    }

    /**
     * 强制重新加载异常类型描述符
     * <p>
     * 等效于调用{@code reload(true)}，清空缓存并重新通过反射获取异常类型
     */
    @Override
    public void reload() {
        reload(true);
    }

    /**
     * 重新加载异常类型描述符（支持强制刷新）
     * <p>
     * 执行流程：
     * <ol>
     *   <li>检查是否需要刷新（force参数或首次加载）</li>
     *   <li>使用同步块确保多线程环境下的安全加载</li>
     *   <li>通过反射获取可执行元素的异常类型信息</li>
     *   <li>转换为TypeDescriptor并缓存结果</li>
     * </ol>
     * 
     * @param force 是否强制刷新缓存，true时忽略当前缓存状态
     * @return 是否成功加载新的类型描述符
     */
    public boolean reload(boolean force) {
        if (force || typeDescriptors == null) {
            synchronized (this) {
                if (force || typeDescriptors == null) {
                    // 获取反射异常类型信息
                    AnnotatedType[] annotatedExceptionTypes = executable.getAnnotatedExceptionTypes();
                    Class<?>[] exceptionTypes = executable.getExceptionTypes();
                    Type[] genericExceptionTypes = executable.getGenericExceptionTypes();
                    
                    // 转换为TypeDescriptor并缓存
                    TypeDescriptor[] typeDescriptors = new TypeDescriptor[exceptionTypes.length];
                    for (int i = 0; i < typeDescriptors.length; i++) {
                        typeDescriptors[i] = new TypeDescriptor(
                                ResolvableType.forType(genericExceptionTypes[i]),
                                exceptionTypes[i],
                                annotatedExceptionTypes[i]
                        );
                    }
                    this.typeDescriptors = typeDescriptors;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否存在异常类型
     * 
     * @return 如果没有异常类型返回true，否则返回false
     */
    @Override
    public final boolean isEmpty() {
        return count() == 0;
    }

    /**
     * 获取异常类型的数量
     * 
     * @return 异常类型的数量
     */
    @Override
    public long count() {
        reload(false);
        return typeDescriptors.length;
    }
}