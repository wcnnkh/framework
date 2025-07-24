package run.soeasy.framework.io;

import java.io.IOException;

/**
 * 数据导出接口，定义将数据导出到可追加目标的功能契约。
 * 该接口为函数式接口，适用于需要将数据导出到文件、流或其他可追加目标的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>目标抽象：支持任意实现{@link Appendable}的导出目标</li>
 *   <li>异常规范：明确声明导出过程中可能抛出的IO异常</li>
 *   <li>函数式设计：单方法接口，便于使用Lambda表达式实现</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>数据报表导出：将数据导出为文本、CSV等格式</li>
 *   <li>日志记录：将日志信息追加到日志文件</li>
 *   <li>网络传输：将数据导出到Socket输出流</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Appendable
 * @see IOException
 */
@FunctionalInterface
public interface Exportable {
    /**
     * 将数据导出到指定的可追加目标。
     * <p>
     * 该方法负责将当前数据导出到实现了{@link Appendable}接口的目标对象，
     * 例如文件、字符串缓冲区或输出流。导出过程中若发生IO错误，将抛出异常。
     * </p>
     * 
     * @param target 导出目标，必须实现{@link Appendable}接口
     * @throws IOException 当导出过程中发生IO错误时抛出
     */
    void export(Appendable target) throws IOException;
}