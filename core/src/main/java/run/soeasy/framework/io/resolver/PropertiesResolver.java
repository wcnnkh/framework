package run.soeasy.framework.io.resolver;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import lombok.NonNull;
import run.soeasy.framework.io.Resource;

/**
 * 属性文件解析器接口，定义属性文件与{@link Properties}对象之间的相互转换操作。
 * 该接口提供资源类型检测、属性解析和持久化功能，支持不同格式的属性文件解析（如.properties、.xml等）。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>类型检测：通过{@link #canResolveProperties(Resource)}判断资源是否可解析</li>
 *   <li>属性解析：通过{@link #resolveProperties(Properties, Resource)}将资源内容加载到Properties</li>
 *   <li>持久化存储：通过{@link #persistenceProperties(Properties, Resource)}将Properties保存到资源</li>
 * </ul>
 *
 * <p><b>实现规范：</b>
 * <ul>
 *   <li>实现类应支持特定格式的属性文件（如.properties、.xml）</li>
 *   <li>resolveProperties和persistenceProperties方法应保持互逆性</li>
 *   <li>对于不支持的资源类型，canResolveProperties应返回false而非抛出异常</li>
 *   <li>处理XML格式时，若格式不符合规范应抛出InvalidPropertiesFormatException</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Properties
 * @see Resource
 * @see java.util.Properties#load(java.io.Reader)
 * @see java.util.Properties#storeToXML(java.io.OutputStream, String)
 */
public interface PropertiesResolver {
    /**
     * 判断资源是否可被当前解析器处理。
     * <p>
     * 通常基于资源类型、文件名后缀或内容特征进行判断，
     * 实现类应快速返回结果，避免进行复杂的IO操作。
     * 
     * @param resource 待检测的资源，不可为null
     * @return true表示资源可被解析
     */
    boolean canResolveProperties(@NonNull Resource resource);

    /**
     * 从资源中解析属性并加载到Properties对象。
     * <p>
     * 实现类应根据资源类型选择合适的加载方式（如.properties使用load，.xml使用loadFromXML），
     * 并确保资源中的所有属性都被正确加载到Properties对象中。
     * 
     * @param properties 目标Properties对象，不可为null
     * @param resource   源资源，不可为null
     * @throws IOException                   读取资源时发生IO异常
     * @throws InvalidPropertiesFormatException 资源格式不符合属性文件规范
     */
    void resolveProperties(@NonNull Properties properties, @NonNull Resource resource)
            throws IOException, InvalidPropertiesFormatException;

    /**
     * 将Properties对象持久化到资源。
     * <p>
     * 实现类应根据资源类型选择合适的存储方式（如.properties使用store，.xml使用storeToXML），
     * 并确保Properties中的所有属性都被正确保存到资源中。
     * 
     * @param properties 源Properties对象，不可为null
     * @param resource   目标资源，不可为null
     * @throws IOException 写入资源时发生IO异常
     */
    void persistenceProperties(@NonNull Properties properties, @NonNull Resource resource) throws IOException;
}