package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor;

/**
 * 属性描述符接口，继承自{@link AccessibleDescriptor}，用于描述对象属性的元数据，
 * 提供属性名称访问和重命名功能，是属性映射和转换的基础元数据接口。
 * <p>
 * 该接口定义了属性的基本描述信息，包括属性名称和访问能力（读写权限），
 * 适用于对象属性的反射访问、数据绑定、类型转换等场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>名称访问：通过{@link #getName()}获取属性名称</li>
 *   <li>名称重命名：通过{@link #rename(String)}创建新名称的描述符</li>
 *   <li>访问能力：继承自{@link AccessibleDescriptor}的读写能力</li>
 *   <li>空数组常量：提供{@link #EMPTY_ARRAY}用于空属性数组场景</li>
 * </ul>
 * </p>
 *
 * <p><b>实现注意事项：</b>
 * <ul>
 *   <li>重命名操作应返回新的描述符实例，保持原描述符不可变</li>
 *   <li>名称应符合目标框架的属性命名规范（如Java Bean规范）</li>
 *   <li>实现类需确保名称的线程安全性（若涉及多线程访问）</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AccessibleDescriptor
 * @see NamedAccessibleDescriptor
 */
public interface PropertyDescriptor extends AccessibleDescriptor {
    
    /** 空属性描述符数组常量，用于表示无属性的场景 */
    PropertyDescriptor[] EMPTY_ARRAY = new PropertyDescriptor[0];

    /**
     * 获取属性名称
     * <p>
     * 返回的名称应符合目标框架的属性命名规范，
     * 例如Java Bean属性名称（首字母小写的驼峰命名）。
     * </p>
     * 
     * @return 属性名称，非空字符串
     */
    String getName();

    /**
     * 创建重命名的属性描述符
     * <p>
     * 该方法返回一个新的属性描述符，保留原描述符的访问能力，
     * 但使用新的属性名称。原描述符实例不会被修改。
     * </p>
     * 
     * @param name 新的属性名称，不可为null
     * @return 重命名的属性描述符实例
     * @throws NullPointerException 若name为null
     * @see NamedAccessibleDescriptor
     */
    default PropertyDescriptor rename(String name) {
        return new NamedAccessibleDescriptor<>(this, name);
    }
}