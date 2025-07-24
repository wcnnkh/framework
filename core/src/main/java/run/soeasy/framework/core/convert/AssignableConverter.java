package run.soeasy.framework.core.convert;

/**
 * 类型赋值转换器，用于处理源类型可以直接赋值给目标类型的场景。
 * 该转换器支持Java类型系统中的直接赋值转换，包括：
 * <ul>
 *   <li>相同类型之间的转换</li>
 *   <li>子类到父类的向上转型</li>
 *   <li>实现类到接口的转换</li>
 *   <li>基本类型的自动装箱/拆箱</li>
 * </ul>
 *
 * <p>该转换器不执行任何实际的数据转换操作，仅验证类型兼容性，
 * 并直接返回源对象。这是一种零成本转换，用于优化不需要数据处理的场景。
 *
 * <p>设计特点：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}提供全局唯一实例</li>
 *   <li>无状态：线程安全，可在多线程环境中共享</li>
 *   <li>高效：直接返回源对象，无需额外处理</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Converter
 * @see TypeDescriptor
 */
class AssignableConverter implements Converter {

    /**
     * 单例实例，用于全局共享
     */
    static final AssignableConverter INSTANCE = new AssignableConverter();

    /**
     * 执行类型转换（直接返回源对象）
     * 
     * @param source 源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 直接返回源对象
     * @throws ConversionException 如果类型不可转换（此实现不会抛出异常，因为在canConvert中已验证）
     */
    @Override
    public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return source;
    }

    /**
     * 判断源类型是否可以赋值给目标类型
     * 
     * @param sourceTypeDescriptor 源类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 如果源类型可以直接赋值给目标类型，返回true；否则返回false
     */
    @Override
    public boolean canConvert(TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) {
        return sourceTypeDescriptor.isAssignableTo(targetTypeDescriptor);
    }
}