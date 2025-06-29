package run.soeasy.framework.xml;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.dom.DocumentException;
import run.soeasy.framework.dom.DocumentOperations;
import run.soeasy.framework.io.AppendableWriter;
import run.soeasy.framework.io.ReadableReader;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * XML模板处理器，提供DOM文档的创建、解析和转换功能，实现了DocumentOperations接口规范。
 * <p>
 * 该类配置了安全解析特性以防御XXE攻击，默认忽略DTD加载、注释和空白字符，
 * 并支持自定义实体解析器和工厂配置。<strong>多线程环境下修改配置会导致线程不安全，</strong>
 * 建议通过ThreadLocal管理实例或确保配置不可变。
 */
@Getter
@Setter
public class XmlTemplate implements DocumentOperations {
    private static final Logger logger = LogManager.getLogger(XmlTemplate.class);
    
    /** 文档构建工厂，用于创建DocumentBuilder实例（非线程安全，多线程修改配置需同步） */
    @NonNull
    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    
    /** 实体解析器，默认忽略DTD加载 */
    @NonNull
    private EntityResolver entityResolver = IgnoreDTDResolver.DEFAULT;
    
    /** 转换器工厂，用于创建Transformer实例（非线程安全，多线程修改配置需同步） */
    @NonNull
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * 初始化模板处理器，配置解析器安全特性和默认参数
     */
    public XmlTemplate() {
        afterProperties();
    }

    /**
     * 初始化后配置工厂参数，包括安全特性和解析选项
     * <p><strong>注意：多线程环境下并发调用此方法或修改配置会导致状态不一致，</strong>
     * 建议在单线程初始化或使用线程安全的配置方式
     */
    public void afterProperties() {
        // 基础解析配置
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setNamespaceAware(false);

        // 安全特性配置（防御XXE攻击）
        try {
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            logger.error("Failed to set XML security features", e);
        }

        try {
            this.transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            logger.error("Failed to set XML security features", e);
        }
    }

    /**
     * 获取配置好的DocumentBuilder实例，自动应用实体解析器
     * @return 初始化后的DocumentBuilder
     * @throws ParserConfigurationException 工厂配置异常
     * <p><strong>线程安全说明：</strong>
     * DocumentBuilderFactory本身非线程安全，建议每个线程独立使用实例
     */
    public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        EntityResolver entityResolver = getEntityResolver();
        if (entityResolver != null) {
            documentBuilder.setEntityResolver(entityResolver);
        }
        return documentBuilder;
    }

    /**
     * 获取转换器实例，用于DOM到XML的转换
     * @return 初始化后的Transformer
     * @throws TransformerConfigurationException 转换器配置异常
     * <p><strong>线程安全说明：</strong>
     * TransformerFactory本身非线程安全，多线程共享实例需注意配置同步
     */
    public Transformer getTransformer() throws TransformerConfigurationException {
        return transformerFactory.newTransformer();
    }

    /**
     * 创建新的空文档
     * @return 新的Document实例
     * @throws DocumentException 文档创建失败时抛出
     */
    @Override
    public Document newDocument() throws DocumentException {
        try {
            return getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new DocumentException("Failed to create new XML document", e);
        }
    }

    /**
     * 解析可读源为DOM文档
     * @param readable 包含XML内容的可读源
     * @return 解析后的Document对象
     * @throws IOException 读取输入时发生I/O异常
     * @throws DocumentException 解析过程中发生文档处理异常
     */
    @Override
    public Document parse(@NonNull Readable readable) throws IOException, DocumentException {
        InputSource inputSource = new InputSource(new ReadableReader(readable));
        try {
            return getDocumentBuilder().parse(inputSource);
        } catch (SAXException | ParserConfigurationException e) {
            throw new DocumentException("XML parsing failed", e);
        }
    }

    /**
     * 将DOM节点转换为XML并写入目标
     * @param source 源DOM节点
     * @param target 输出目标
     * @throws IOException 写入输出时发生I/O异常
     * @throws DocumentException 转换过程中发生文档处理异常
     */
    @Override
    public void transform(@NonNull Node source, @NonNull Appendable target) throws IOException, DocumentException {
        StreamResult result = new StreamResult(new AppendableWriter(target));
        DOMSource domSource = new DOMSource(source);
        try {
            getTransformer().transform(domSource, result);
        } catch (TransformerException e) {
            throw new DocumentException("XML transformation failed", e);
        }
    }
}