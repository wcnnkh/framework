package run.soeasy.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import lombok.ToString;

/**
 * 实现了{@link AnnotatedElement}接口的注解数组包装器，
 * 用于将一组注解封装为一个可被反射访问的元素。
 * 
 * <p>该类允许将注解集合作为一个元素进行处理，支持通过反射API查询注解信息。
 * 提供了多种构造方式，可从现有元素或直接从注解数组创建实例。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>不可变性：创建后注解内容不可变</li>
 *   <li>保护性复制：返回注解数组的克隆以防止外部修改</li>
 *   <li>智能构造：支持从其他AnnotatedElement派生注解内容</li>
 *   <li>空值安全：自动处理null输入并转换为空注解数组</li>
 * </ul>
 * 
 * @author soeasy.run
 */
@ToString
public class AnnotationArrayAnnotatedElement implements AnnotatedElement, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 该元素上的所有注解
     */
    private final Annotation[] annotations;
    
    /**
     * 该元素上直接声明的注解
     */
    private final Annotation[] declaredAnnotations;

    /**
     * 从现有AnnotatedElement创建包装器实例。
     * 
     * <p>根据源元素类型执行不同的处理策略：
     * <ul>
     *   <li>如果源元素为null或EmptyAnnotatedElement，创建空注解集合</li>
     *   <li>如果源元素是AnnotationArrayAnnotatedElement，复用其注解数组</li>
     *   <li>其他情况，复制源元素的注解</li>
     * </ul>
     * 
     * @param annotatedElement 源注解元素，允许为null
     */
    public AnnotationArrayAnnotatedElement(AnnotatedElement annotatedElement) {
        if (annotatedElement == null || annotatedElement instanceof EmptyAnnotatedElement) {
            this.annotations = AnnotationUtils.EMPTY;
            this.declaredAnnotations = AnnotationUtils.EMPTY;
        } else if (annotatedElement instanceof AnnotationArrayAnnotatedElement) {
            this.annotations = ((AnnotationArrayAnnotatedElement) annotatedElement).annotations;
            this.declaredAnnotations = ((AnnotationArrayAnnotatedElement) annotatedElement).declaredAnnotations;
        } else {
            this.annotations = annotatedElement.getAnnotations();
            this.declaredAnnotations = annotatedElement.getDeclaredAnnotations();
        }
    }

    /**
     * 使用相同的注解数组同时初始化所有注解和直接声明的注解。
     * 
     * @param annotations 注解数组，null会被转换为空数组
     */
    public AnnotationArrayAnnotatedElement(Annotation[] annotations) {
        this(annotations, annotations);
    }

    /**
     * 使用不同的注解数组分别初始化所有注解和直接声明的注解。
     * 
     * @param annotations 所有注解数组，null会被转换为空数组
     * @param declaredAnnotations 直接声明的注解数组，null会被转换为空数组
     */
    public AnnotationArrayAnnotatedElement(Annotation[] annotations, Annotation[] declaredAnnotations) {
        this.annotations = annotations == null ? AnnotationUtils.EMPTY : annotations;
        this.declaredAnnotations = declaredAnnotations == null ? AnnotationUtils.EMPTY : declaredAnnotations;
    }

    /**
     * 获取该元素上的指定类型注解。
     * 如果存在多个相同类型的注解，将使用合成机制生成一个合并的注解实例。
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 注解实例或null（如果不存在）
     * @see AnnotationUtils#getAnnotation(Class, Annotation...)
     */
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return AnnotationUtils.getAnnotation(annotationClass, annotations);
    }

    /**
     * 获取该元素上直接声明的指定类型注解。
     * 如果存在多个相同类型的注解，将使用合成机制生成一个合并的注解实例。
     * 
     * @param <T> 注解类型
     * @param annotationClass 注解类
     * @return 注解实例或null（如果不存在）
     * @see AnnotationUtils#getAnnotation(Class, Annotation...)
     */
    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return AnnotationUtils.getAnnotation(annotationClass, declaredAnnotations);
    }

    /**
     * 获取该元素上的所有注解。
     * 
     * @return 注解数组的克隆，保证内部状态不被外部修改
     */
    @Override
    public Annotation[] getAnnotations() {
        return annotations.clone();
    }

    /**
     * 获取该元素上直接声明的所有注解。
     * 
     * @return 注解数组的克隆，保证内部状态不被外部修改
     */
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return declaredAnnotations.clone();
    }
}