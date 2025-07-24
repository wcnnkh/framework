package run.soeasy.framework.aop;

import java.lang.reflect.AnnotatedElement;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.annotation.AnnotatedElementWrapper;
import run.soeasy.framework.core.execute.ExecutableTemplate;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * AOP代理核心接口，继承自{@link ExecutableTemplate}和{@link AnnotatedElementWrapper<AnnotatedElement>}，
 * 定义了代理对象的执行规范与注解元素访问能力，是AOP框架中封装代理逻辑、执行目标方法并整合注解信息的核心契约。
 * 
 * <p>该接口整合了可执行模板（支持参数化执行）与注解元素包装（支持访问代理目标的注解信息），
 * 为代理对象提供统一的执行入口（{@link #execute()}系列方法）和注解元数据访问（{@link #getSource()}），
 * 适用于各类AOP场景（如方法拦截、事务管理、日志增强等）。
 * 
 * @author soeasy.run
 * @see ExecutableTemplate
 * @see AnnotatedElementWrapper
 * @see AnnotatedElement
 */
public interface Proxy extends ExecutableTemplate, AnnotatedElementWrapper<AnnotatedElement> {

    /**
     * 无参执行代理逻辑（默认实现）
     * 
     * <p>调用带参数的{@link #execute(Class[], Object...)}方法，传入空参数类型数组和空参数数组，
     * 适用于目标方法无参数的场景，简化无参调用的代码编写。
     * 
     * @return 代理执行的结果（目标方法的返回值或增强后的结果）
     */
    @Override
    default Object execute() {
        return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
    }

    /**
     * 执行代理逻辑（核心方法，需实现类提供具体实现）
     * 
     * <p>该方法是代理执行的入口，接收参数类型数组和实际参数数组，用于匹配目标方法并执行，
     * 实现类需在此方法中封装AOP增强逻辑（如前置通知、目标方法调用、后置处理等）。
     * 
     * @param parameterTypes 目标方法的参数类型数组（非空，用于精确匹配方法签名）
     * @param args 实际传入的参数数组（非空，数量与类型需与parameterTypes匹配）
     * @return 代理执行的结果（目标方法的返回值或经增强处理后的结果）
     */
    @Override
    Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args);

    /**
     * 获取代理的注解元素源（默认返回返回值类型描述符对应的注解元素）
     * 
     * <p>实现{@link AnnotatedElementWrapper}接口，返回代理目标的可注解元素（如目标方法、目标类等），
     * 用于访问目标元素上的注解信息（如@Transactional、@Log等），支持AOP增强的条件判断。
     * 
     * @return 代理目标的注解元素（如Method、Class等，非空）
     */
    @Override
    default AnnotatedElement getSource() {
        return getReturnTypeDescriptor();
    }
}