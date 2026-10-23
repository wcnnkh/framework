package run.soeasy.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringFormat;

/**
 * DocumentOperations 接口定义了对 W3C DOM 文档进行操作的标准方法集合，
 * 包括文档创建、解析和转换功能，同时实现了 StringFormat 接口以支持节点与字符串之间的转换。
 * 
 * <p>该接口提供了将 Readable 源解析为 DOM 文档的能力，
 * 以及将 DOM 节点转换为可写入 Appendable 目标的格式化输出的功能。
 * 所有操作都支持异常处理机制，确保在转换过程中出现错误时能够提供明确的错误信息。
 * 
 * <p>实现类应提供具体的文档解析和转换策略，例如使用 SAX、DOM 或其他解析器，
 * 以及不同的输出格式（如 XML、HTML 等）。
 */
public interface DocumentOperations extends StringFormat<Node> {

    /**
     * 从 Readable 源解析内容并转换为 DOM 节点。
     * 该方法会将 Readable 中的内容解析为完整的 XML 文档，
     * 并返回文档的根元素（即文档元素）。
     * 
     * @param readable 包含文档内容的 Readable 源
     * @param targetTypeDescriptor 目标类型描述符
     * @return 解析后文档的根元素节点
     * @throws ConversionException 当转换过程中发生错误时抛出
     * @throws IOException 当读取输入源时发生 I/O 错误时抛出
     */
    @Override
    default Node from(@NonNull Readable readable, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException, IOException {
        Document document;
        try {
            document = parse(readable);
        } catch (DocumentException e) {
            throw new ConversionFailedException(targetTypeDescriptor, targetTypeDescriptor, readable, e);
        }
        return document.getDocumentElement();
    }

    /**
     * 创建一个新的空 DOM 文档实例。
     * 该文档实例可用于构建新的 XML 结构。
     * 
     * @return 新创建的 DOM 文档实例
     * @throws DocumentException 当创建文档过程中发生错误时抛出
     */
    Document newDocument() throws DocumentException;

    /**
     * 从 Readable 源解析内容并构建 DOM 文档。
     * 该方法会读取 Readable 中的全部内容，并将其解析为 W3C DOM 文档对象。
     * 
     * @param readable 包含文档内容的 Readable 源
     * @return 解析后得到的 DOM 文档对象
     * @throws IOException 当读取输入源时发生 I/O 错误时抛出
     * @throws DocumentException 当解析文档过程中发生错误时抛出
     */
    Document parse(@NonNull Readable readable) throws IOException, DocumentException;

    /**
     * 将 DOM 节点转换为字符流并写入 Appendable 目标。
     * 该方法会将指定的 Node 及其所有子节点以格式化的方式输出到 Appendable 中。
     * 
     * @param source 要转换的源 DOM 节点
     * @param sourceTypeDescriptor 源类型描述符
     * @param appendable 用于接收输出的 Appendable 目标
     * @throws ConversionException 当转换过程中发生错误时抛出
     * @throws IOException 当写入输出目标时发生 I/O 错误时抛出
     */
    @Override
    default void to(@NonNull Node source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Appendable appendable)
            throws ConversionException, IOException {
        try {
            transform(source, appendable);
        } catch (DocumentException e) {
            throw new ConversionFailedException(sourceTypeDescriptor, TypeDescriptor.forObject(appendable), source, e);
        }
    }

    /**
     * 将 DOM 节点转换为字符流并写入目标 Appendable。
     * 该方法提供了将 Node 对象转换为字符串表示形式的能力，
     * 具体格式（如 XML、HTML 等）由实现类决定。
     * 
     * @param source 要转换的源 DOM 节点
     * @param target 用于接收转换结果的 Appendable 目标
     * @throws IOException 当写入输出目标时发生 I/O 错误时抛出
     * @throws DocumentException 当转换文档过程中发生错误时抛出
     */
    void transform(@NonNull Node source, @NonNull Appendable target) throws IOException, DocumentException;
}