package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.annotation.AnnotationArrayAnnotatedElement;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execute.ParameterTemplate;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 反射可执行元素实现类，继承自{@link AbstractReflectionExecutable}并实现{@link AnnotatedElementWrapper}接口，
 * 用于封装Java反射中的Executable对象（Method或Constructor），提供可执行元素的元数据描述和注解处理能力。
 * <p>
 * 该类采用懒加载和缓存机制，延迟创建并缓存方法的声明类型、异常类型、参数模板和返回类型等元数据，
 * 以提高反射操作的性能。同时支持对方法注解的访问和处理，实现了注解元素的包装功能。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Executable对象封装为框架可识别的元数据</li>
 *   <li>元数据缓存：采用双重检查锁机制实现元数据的延迟初始化和缓存</li>
 *   <li>注解处理：实现注解元素包装接口，支持对方法注解的统一访问</li>
 *   <li>类型描述：提供类型描述符（TypeDescriptor）体系的元数据访问</li>
 * </ul>
 *
 * <p><b>性能优化：</b>
 * <ul>
 *   <li>延迟初始化：首次访问时才创建元数据对象</li>
 *   <li>线程安全：使用双重检查锁确保多线程环境下的安全初始化</li>
 *   <li>缓存机制：元数据创建后会被缓存，避免重复创建</li>
 *   <li>失效机制：当源Executable对象变更时，自动清空缓存</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>反射调用：作为方法或构造函数的元数据提供者</li>
 *   <li>AOP框架：获取方法注解和参数信息用于切面增强</li>
 *   <li>依赖注入：解析方法参数类型和注解用于依赖注入</li>
 *   <li>API文档生成：提取方法签名和注解信息生成文档</li>
 *   <li>参数验证：基于方法参数注解实现参数验证逻辑</li>
 * </ul>
 *
 * @param <T> 反射Executable类型，必须是{@link Executable}的子类（如Method或Constructor）
 * @author soeasy.run
 * @see AbstractReflectionExecutable
 * @see AnnotatedElementWrapper
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 */
public class ReflectionExecutable<T extends Executable> extends AbstractReflectionExecutable<T>
        implements AnnotatedElementWrapper<T> {

    /**
     * 声明类的类型描述符，使用双重检查锁延迟初始化
     */
    @NonNull
    private transient volatile TypeDescriptor declaringTypeDescriptor;

    /**
     * 异常类型描述符集合，使用双重检查锁延迟初始化
     */
    @NonNull
    private transient volatile Elements<TypeDescriptor> exceptionTypeDescriptors;

    /**
     * 参数模板，使用双重检查锁延迟初始化
     */
    private transient volatile ParameterTemplate parameterTemplate;

    /**
     * 返回类型描述符，使用双重检查锁延迟初始化
     */
    @NonNull
    private transient volatile TypeDescriptor returnTypeDescriptor;

    /**
     * 构造函数，初始化反射可执行元素
     * 
     * @param member 反射Executable对象，不可为null
     */
    public ReflectionExecutable(@NonNull T member) {
        super(member);
    }

    /**
     * 设置反射Executable对象，并清空已缓存的元数据
     * 
     * @param source 反射Executable对象，不可为null
     */
    @Override
    public synchronized void setSource(@NonNull T source) {
        super.setSource(source);
        this.declaringTypeDescriptor = null;
        this.exceptionTypeDescriptors = null;
        this.parameterTemplate = null;
        this.returnTypeDescriptor = null;
    }

    /**
     * 获取声明类的类型描述符
     * <p>
     * 该方法使用双重检查锁实现延迟初始化，首次调用时创建并缓存声明类的类型描述符，
     * 后续调用直接返回缓存结果，提高性能。
     * 
     * @return 声明类的类型描述符
     */
    @Override
    public TypeDescriptor getDeclaringTypeDescriptor() {
        if (declaringTypeDescriptor == null) {
            synchronized (this) {
                if (declaringTypeDescriptor == null) {
                    declaringTypeDescriptor = new TypeDescriptor(
                            ResolvableType.forType(getSource().getDeclaringClass()), 
                            getSource().getDeclaringClass(),
                            new AnnotationArrayAnnotatedElement(getSource().getDeclaredAnnotations()));
                }
            }
        }
        return declaringTypeDescriptor;
    }

    /**
     * 获取异常类型描述符集合
     * <p>
     * 该方法使用双重检查锁实现延迟初始化，首次调用时创建并缓存异常类型描述符集合，
     * 后续调用直接返回缓存结果，提高性能。
     * 
     * @return 异常类型描述符集合
     */
    @Override
    public Elements<TypeDescriptor> getExceptionTypeDescriptors() {
        if (exceptionTypeDescriptors == null) {
            synchronized (this) {
                if (exceptionTypeDescriptors == null) {
                    exceptionTypeDescriptors = new ExecutableExceptionTypeDescriptors(getSource());
                }
            }
        }
        return exceptionTypeDescriptors;
    }

    /**
     * 获取参数模板
     * <p>
     * 该方法使用双重检查锁实现延迟初始化，首次调用时创建并缓存参数模板，
     * 后续调用直接返回缓存结果，提高性能。
     * 
     * @return 参数模板
     */
    @Override
    public ParameterTemplate getParameterTemplate() {
        if (parameterTemplate == null) {
            synchronized (this) {
                if (parameterTemplate == null) {
                    parameterTemplate = new ExecutableParameterTemplate(getSource());
                }
            }
        }
        return parameterTemplate;
    }

    /**
     * 获取返回类型描述符
     * <p>
     * 该方法使用双重检查锁实现延迟初始化，首次调用时创建并缓存返回类型描述符，
     * 后续调用直接返回缓存结果，提高性能。
     * 
     * @return 返回类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        if (returnTypeDescriptor == null) {
            synchronized (this) {
                if (returnTypeDescriptor == null) {
                    returnTypeDescriptor = TypeDescriptor.forExecutableReturnType(getSource());
                }
            }
        }
        return returnTypeDescriptor;
    }
}