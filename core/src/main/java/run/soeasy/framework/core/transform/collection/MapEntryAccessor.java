package run.soeasy.framework.core.transform.collection;

import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * Map条目值访问器，实现{@link TypedValueAccessor}接口，
 * 用于类型安全地访问和操作Map中特定条目的值，支持自动类型转换。
 * <p>
 * 该访问器通过包装Map实例和键，提供对Map条目的统一访问接口，
 * 适用于需要将Map条目视为独立属性进行操作的场景，如数据绑定、表单处理等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全访问：通过{@link TypeDescriptor}确保值的类型一致性</li>
 *   <li>自动类型转换：使用{@link Converter}实现值的类型转换</li>
 *   <li>读写支持：同时提供get和set操作，满足双向数据交互需求</li>
 *   <li>空值安全：通过{@link #isReadable()}方法判断条目是否存在</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>表单数据与Map之间的双向绑定</li>
 *   <li>Map条目值的类型安全操作</li>
 *   <li>动态属性访问（如配置项的读取与修改）</li>
 *   <li>数据转换过程中的Map条目处理</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypedValueAccessor
 * @see Converter
 * @see TypeDescriptor
 */
@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class MapEntryAccessor implements TypedValueAccessor {
    
    /** 被访问的Map实例，不可为null */
    @NonNull
    private final Map map;
    
    /** Map中的键，用于定位具体条目，不可为null */
    @NonNull
    private final Object key;
    
    /** Map的类型描述符，用于获取值的类型信息，不可为null */
    @NonNull
    private final TypeDescriptor mapTypeDescriptor;
    
    /** 类型转换器，用于值的类型转换，不可为null */
    @NonNull
    private final Converter converter;

    /**
     * 获取Map条目的值并转换为目标类型
     * <p>
     * 该方法从Map中获取指定键的值，若值存在则使用转换器将其转换为Map值类型，
     * 若值不存在或转换失败则抛出异常。
     * 
     * @return 转换后的Map条目值
     * @throws ConversionException 当值存在但类型转换失败时抛出
     */
    @Override
    public Object get() throws ConversionException {
        Object value = map.get(key);
        return converter.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
    }

    /**
     * 设置Map条目的值（支持类型转换）
     * <p>
     * 该方法将输入值转换为Map值类型后，设置到Map的指定键位置，
     * 若Map不支持修改操作则抛出异常。
     * 
     * @param value 要设置的值
     * @throws UnsupportedOperationException 当Map不支持修改操作时抛出
     */
    @SuppressWarnings("unchecked")
    @Override
    public void set(Object value) throws UnsupportedOperationException {
        Object target = converter.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
        map.put(key, target);
    }

    /**
     * 判断Map条目是否可读
     * <p>
     * 该方法通过检查Map是否包含指定键来判断条目是否可读，
     * 与{@link #get()}方法配合使用可避免空指针异常。
     * 
     * @return 若Map包含指定键返回true，否则返回false
     */
    @Override
    public boolean isReadable() {
        return map.containsKey(key);
    }

    /**
     * 判断Map条目是否可写
     * <p>
     * 该方法始终返回true，因为Map理论上支持写操作，
     * 实际是否可写由Map实现决定（如不可变Map会在set时抛出异常）。
     * 
     * @return 始终返回true
     */
    @Override
    public boolean isWriteable() {
        return true;
    }

    /**
     * 获取期望的值类型描述符
     * <p>
     * 返回Map值类型的描述符，用于指示set操作时期望的值类型。
     * 
     * @return Map值类型的描述符
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() {
        return mapTypeDescriptor.getMapValueTypeDescriptor();
    }

    /**
     * 获取返回值的类型描述符
     * <p>
     * 返回Map值类型的描述符，用于指示get操作返回值的类型。
     * 
     * @return Map值类型的描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return mapTypeDescriptor.getMapValueTypeDescriptor();
    }
}