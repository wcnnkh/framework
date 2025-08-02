package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Member;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.execute.ExecutableMetadata;

/**
 * 基于反射的可执行元素元数据抽象基类，实现{@link ExecutableMetadata}接口和{@link Wrapper}接口，
 * 为Java反射中的Method和Constructor提供统一的元数据描述和操作接口。
 * <p>
 * 该类封装了Java反射中的Member对象（Method或Constructor），并提供了获取方法名称、
 * 声明类等基础元数据的能力，是框架中反射调用体系的核心基础组件。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Member对象封装为框架可识别的元数据</li>
 *   <li>元数据访问：提供统一接口访问方法或构造函数的元数据信息</li>
 *   <li>类型安全：通过泛型约束确保封装的反射对象类型一致性</li>
 *   <li>声明类处理：提供便捷方法获取和描述声明类信息</li>
 * </ul>
 *
 * <p><b>子类实现：</b>
 * 子类需实现以下核心方法：
 * <ul>
 *   <li>{@link #canExecuted(Class[])}：判断是否可以使用指定参数类型执行</li>
 *   <li>{@link #getParameterTemplate()}：获取参数模板描述</li>
 *   <li>{@link #getExceptionTypeDescriptors()}：获取异常类型描述符集合</li>
 * </ul>
 *
 * @param <T> 反射Member类型，必须是{@link Member}的子类（如Method或Constructor）
 * @author soeasy.run
 * @see ExecutableMetadata
 * @see Wrapper
 * @see java.lang.reflect.Method
 * @see java.lang.reflect.Constructor
 */
@Data
public abstract class AbstractReflectionExecutable<T extends Member> implements ExecutableMetadata, Wrapper<T> {
    /**
     * 封装的反射Member对象（Method或Constructor）
     */
    @NonNull
    protected transient T source;

    /**
     * 构造函数，初始化反射可执行元素元数据
     * 
     * @param source 反射Member对象，不可为null
     */
    public AbstractReflectionExecutable(T source) {
        setSource(source);
    }

    /**
     * 获取可执行元素的名称
     * <p>
     * 对于Method，返回方法名；对于Constructor，返回类名。
     * 
     * @return 可执行元素的名称
     */
    @Override
    public String getName() {
        return getSource().getName();
    }

    /**
     * 获取声明该可执行元素的类
     * 
     * @return 声明类的Class对象
     */
    public Class<?> getDeclaringClass() {
        return getSource().getDeclaringClass();
    }

    /**
     * 获取声明类的类型描述符
     * 
     * @return 声明类的类型描述符
     */
    @Override
    public TypeDescriptor getDeclaringTypeDescriptor() {
        return TypeDescriptor.valueOf(getDeclaringClass());
    }

    /**
     * 设置封装的反射Member对象
     * 
     * @param source 反射Member对象，不可为null
     */
    public void setSource(@NonNull T source) {
        this.source = source;
    }
}