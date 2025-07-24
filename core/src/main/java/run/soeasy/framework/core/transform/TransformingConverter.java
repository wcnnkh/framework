package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;

/**
 * 转换型转换器，实现{@link Converter}和{@link Transformer}接口，
 * 支持将源对象转换为目标对象类型，同时支持对象属性的直接转换操作。
 * <p>
 * 该转换器通过组合{@link Transformer}实现属性转换逻辑，
 * 并使用{@link InstanceFactory}创建目标对象实例，
 * 适用于需要将转换逻辑与对象创建解耦的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换支持：同时实现Converter和Transformer接口</li>
 *   <li>实例工厂注入：支持通过{@link InstanceFactory}创建目标实例</li>
 *   <li>转换开关控制：通过enable字段动态启用/禁用转换功能</li>
 *   <li>异常封装：将Transformer的转换异常封装为Converter异常</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>线程安全：未对enable字段和transformer字段添加同步机制，多线程环境可能出现竞态条件</li>
 *   <li>空值风险：transformer字段未进行空值校验，当transformer为null时调用会抛出NPE</li>
 *   <li>异常处理：convert方法未捕获InstanceFactory.newInstance可能抛出的异常</li>
 *   <li>性能优化：默认使用反射实例工厂{@link InstanceFactorySupporteds.REFLECTION}，可考虑缓存实例</li>
 * </ul>
 * </p>
 *
 * @param <S> 源对象类型
 * @param <T> 目标对象类型
 * 
 * @author soeasy.run
 * @see Converter
 * @see Transformer
 * @see InstanceFactory
 */
@Getter
@Setter
public class TransformingConverter<S, T> implements Converter, Transformer {
    
    /** 实例工厂，用于创建目标对象实例，默认使用反射实现 */
    @NonNull
    private InstanceFactory instanceFactory = InstanceFactorySupporteds.REFLECTION;
    
    /** 实际执行属性转换的转换器，若为null则转换功能失效 */
    private Transformer transformer;
    
    /** 转换功能开关，true表示启用转换，false表示禁用 */
    private boolean enable = true;

    /**
     * 判断是否支持源类型到目标类型的转换
     * <p>
     * 检查条件：
     * <ol>
     *   <li>转换功能已启用({@link #enable}为true)</li>
     *   <li>实例工厂可创建目标类型实例</li>
     *   <li>内部{@link #transformer}支持该转换</li>
     * </ol>
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示支持转换，否则false
     * @throws NullPointerException 若参数为null
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return enable && instanceFactory.canInstantiated(targetTypeDescriptor.getResolvableType())
                && canTransform(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 判断是否支持源类型到目标类型的属性转换
     * <p>
     * 检查条件：
     * <ol>
     *   <li>转换功能已启用({@link #enable}为true)</li>
     *   <li>{@link #transformer}非null且支持该转换</li>
     * </ol>
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示支持转换，否则false
     * @throws NullPointerException 若参数为null或transformer为null时调用
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return enable && transformer != null && transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行对象属性转换操作
     * <p>
     * 先检查转换可行性，再委托给{@link #transformer}执行实际转换
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示转换成功，否则false
     * @throws ConversionException 转换过程中抛出的异常
     * @throws NullPointerException 若参数为null或transformer为null时调用
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        if (!canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
            return false;
        }

        return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
    }

    /**
     * 执行类型转换并返回新创建的目标对象
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>检查转换可行性</li>
     *   <li>通过{@link #instanceFactory}创建目标对象实例</li>
     *   <li>调用{@link #transform}方法执行属性转换</li>
     *   <li>返回转换后的目标对象</li>
     * </ol>
     * 
     * @param source 源对象，可为null（由InstanceFactory处理null场景）
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的目标对象实例
     * @throws ConverterNotFoundException 不支持该转换时抛出
     * @throws NullPointerException 若参数为null
     * @throws RuntimeException 包装InstanceFactory.newInstance抛出的异常
     */
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        if (!canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
            throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
        }

        Object target = instanceFactory.newInstance(targetTypeDescriptor.getResolvableType());
        transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
        return target;
    }
}