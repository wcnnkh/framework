package run.soeasy.framework.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.PipelineWrapper;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * PDF 操作核心封装类
 * <p>
 * 核心定位：基于框架 Pipeline 机制，封装 Apache PDFBox 的 {@link PDDocument}
 * 核心能力，整合文档加载、复用、流式处理与资源自动管理， 解决 PDF 操作中资源泄露风险与流式编程需求的矛盾，同时兼容框架 Pipeline
 * 生态的链式调用风格。 类名设计为 {@link PDF}，旨在提供极简的 API 入口，让开发者以最直观的方式操作 PDF 文档。
 * </p>
 * <p>
 * 核心特性： 1. 灵活构建模式：支持"新建空文档"、"加载本地文件"、"复用已有实例"、"自定义加载逻辑"四种场景，覆盖绝大多数 PDF 操作需求； 2.
 * 智能资源管理：根据构建方式自动适配生命周期管理——复用已有文档时由调用方掌控关闭时机，新建/加载文档时自动注册关闭回调，确保资源可靠释放； 3.
 * 便捷功能封装：内置页面合并、文档保存、内容读取、文档拆分等高频操作，无需手动处理 {@link PDDocument} 底层 API； 4.
 * 流式编程兼容：完全遵循 Pipeline 接口规范，支持链式调用扩展自定义处理逻辑。
 * </p>
 * <p>
 * 依赖说明： - 基础依赖：Apache PDFBox（提供 {@link PDDocument}、{@link PDPage} 等核心类）； -
 * 框架依赖：run.soeasy.framework 核心模块（提供 {@link Pipeline}、{@link ThrowingSupplier}
 * 等函数式接口）； - 辅助依赖：Lombok（简化 getter 与构造器生成）。
 * </p>
 * <p>
 * 使用场景示例（API 极简设计）： 1. 快速加载本地 PDF 并读取内容：PDF.build(new
 * File("test.pdf")).read(listener); 2. 新建文档并合并其他 PDF
 * 页面：PDF.build().addPages(otherPdf).save(new File("merged.pdf")); 3. 复用已有
 * PDDocument 实例：PDDocument doc = PDDocument.load(...); PDF.build(doc).split();
 * 4. 自定义加载逻辑（如带密码的 PDF）：PDF.build(() -&gt; PDDocument.load(pdfFile, password));
 * </p>
 */
@RequiredArgsConstructor // Lombok 注解：生成包含所有 @NonNull 字段的构造器，用于初始化核心依赖 Pipeline
@Getter // Lombok 注解：生成所有字段的 getter 方法，仅暴露 source 供框架扩展或自定义操作
public class PDF implements PipelineWrapper<PDDocument, IOException, Pipeline<PDDocument, IOException>> {

	/**
	 * 构建空 PDF 文档实例
	 * <p>
	 * 内部通过 {@link PDDocument} 创建空白文档，自动注册关闭回调，适用于需要手动添加页面、构建新 PDF 的场景。
	 * 文档生命周期由当前实例管理，无需手动调用 {@link PDDocument#close()}。
	 * </p>
	 *
	 * @return PDF - 空白文档实例，可后续通过 addPages 等方法添加内容
	 */
	public static PDF build() {
		return build(PDDocument::new);
	}

	/**
	 * 基于本地 PDF 文件构建实例（常规快捷场景）
	 * <p>
	 * 内部通过 {@link PDDocument#load(File)} 加载指定 PDF 文件，自动注册关闭回调，确保资源不泄露。
	 * 适用于直接处理本地已存在的 PDF 文件，无需手动处理文档加载与关闭逻辑。
	 * </p>
	 *
	 * @param pdf 待加载的 PDF 文件（非空校验：避免空指针；前置条件：文件路径有效、当前用户具备读取权限、文件为合法 PDF 格式）
	 * @return PDF - 已加载文件的实例，可直接执行读取、拆分、合并等操作
	 */
	public static PDF build(@NonNull File pdf) {
		// 委托自定义供应者构建方法，复用自动关闭逻辑
		return build(() -> PDDocument.load(pdf));
	}

	/**
	 * 基于已有 {@link PDDocument} 实例构建（复用场景）
	 * <p>
	 * 核心特性：不自动关闭传入的文档实例，生命周期完全由调用方掌控。 适用于以下场景：1. 文档已手动加载且需多次复用；2. 需自定义关闭时机；3.
	 * 多模块共享同一文档实例。 注意：调用方必须在使用完毕后手动调用 {@link PDDocument#close()}，否则会导致资源泄露。
	 * </p>
	 *
	 * @param document 已初始化的 {@link PDDocument} 实例（非空校验：避免空指针；前置条件：文档已成功加载、未被关闭）
	 * @return PDF - 包装已有文档的实例，可复用文档执行流式操作
	 */
	public static PDF build(@NonNull PDDocument document) {
		// 直接包装已有文档，不添加关闭回调（生命周期由调用方管理）
		Pipeline<PDDocument, IOException> pipeline = Pipeline.forSupplier(() -> document);
		return new PDF(pipeline);
	}

	/**
	 * 基于自定义文档供应者构建（灵活加载场景）
	 * <p>
	 * 支持自定义 {@link PDDocument} 的加载逻辑，自动注册 {@link PDDocument#close()}
	 * 关闭回调，实例操作完毕后（无论成功/失败）自动释放资源。 适用于复杂加载场景：带密码的 PDF
	 * 加载、输入流加载（如网络文件、内存流）、自定义配置的文档加载等。
	 * </p>
	 *
	 * @param documentSupplier 文档供应者函数（非空校验：避免空指针；功能要求：实现文档加载逻辑，返回已初始化、可操作的
	 *                         {@link PDDocument} 实例；可能抛出 IOException）
	 * @return PDF - 基于自定义逻辑加载的文档实例，资源自动管理
	 */
	public static PDF build(@NonNull ThrowingSupplier<PDDocument, IOException> documentSupplier) {
		// 构建 Pipeline 并绑定关闭回调，确保文档资源自动释放
		Pipeline<PDDocument, IOException> pipeline = documentSupplier.onClose((document) -> document.close());
		return new PDF(pipeline);
	}

	/**
	 * 核心委托 Pipeline 实例
	 * <p>
	 * 封装了 {@link PDDocument} 的供应逻辑（加载/复用/新建）与生命周期回调（自动关闭），是所有操作的底层依赖。 所有对外暴露的方法（如
	 * addPages、save、read）均通过该实例委托执行，确保流式处理与资源管理的一致性。
	 */
	@NonNull
	private final Pipeline<PDDocument, IOException> source;

	/**
	 * 合并另一个 PDF 文档的所有页面到当前文档
	 * <p>
	 * 内部通过 {@link PDPageTree} 遍历源文档的所有页面，逐一添加到当前文档中，支持链式调用后续操作（如保存）。
	 * 自动为合并后的实例注册源文档的关闭回调，确保被合并的文档资源也能可靠释放。
	 * </p>
	 *
	 * @param pdf 待合并的 PDF 实例（非空校验：避免空指针；前置条件：实例对应的文档已加载、可操作）
	 * @return PDF - 合并后的文档实例，可继续执行保存、添加其他页面等操作
	 */
	public PDF addPages(@NonNull PDF pdf) {
		Pipeline<PDDocument, IOException> pipeline = map((e) -> {
			PDFUtils.merge(e, Arrays.asList(pdf.get()).iterator());
			return e;
		});
		pipeline = pipeline.onClose(pdf::close);
		return new PDF(pipeline);
	}

	public PDF addPages(File pdf) {
		return addPages(PDF.build(pdf));
	}

	/**
	 * 将当前 PDF 文档保存到指定文件
	 * <p>
	 * 内部通过 {@link PDDocument#save(File)} 执行保存操作，仅当文档实例存在时才执行保存（避免空指针）。
	 * 适用于新建文档、合并后的文档、修改后的文档的持久化存储。
	 * </p>
	 *
	 * @param file 保存目标文件（非空校验：避免空指针；注意事项：若文件已存在会被覆盖，需确保当前用户具备目标路径的写入权限）
	 * @throws IOException 保存过程中发生的 IO 异常（如文件路径不存在、权限不足、文档损坏等）
	 */
	public void save(@NonNull File file) throws IOException {
		optional().ifPresent((e) -> e.save(file));
	}

	/**
	 * 读取 PDF 文档的所有页面内容
	 * <p>
	 * 委托 {@link PDFUtils} 执行读取逻辑，通过 {@link PDFReadListener} 回调返回读取结果（如文本、图片、元数据等）。
	 * 支持自定义读取逻辑扩展（通过实现 {@link PDFReadListener} 接口），适用于需要遍历所有页面内容的场景。
	 * </p>
	 *
	 * @param readListener 读取结果监听器（非空校验：避免空指针；功能要求：实现页面内容处理逻辑，接收读取到的文档内容）
	 * @throws IOException 读取过程中发生的 IO 异常（如文档损坏、读取权限不足等）
	 */
	public void read(@NonNull PDFReadListener readListener) throws IOException {
		optional().ifPresent((document) -> {
			PDFUtils.read(document, readListener);
		});
	}

	/**
	 * 读取 PDF 文档指定页面的内容
	 * <p>
	 * 重载 read 方法，支持指定具体页面索引（索引从 0 开始），仅读取目标页面内容，提升读取效率。 委托 {@link PDFUtils}
	 * 执行定向读取逻辑，通过 {@link PDFReadListener} 回调返回指定页面的内容。
	 * </p>
	 *
	 * @param readListener 读取结果监听器（非空校验：避免空指针；功能要求：实现页面内容处理逻辑）
	 * @param pageIndexs   目标页面索引数组（非空校验：避免空指针；有效范围：0 ≤ 索引 &lt; 文档总页数，超出范围的索引会被忽略）
	 * @throws IOException 读取过程中发生的 IO 异常（如文档损坏、读取权限不足等）
	 */
	public void read(@NonNull PDFReadListener readListener, @NonNull int... pageIndexs) throws IOException {
		optional().ifPresent((document) -> {
			PDFUtils.read(document, readListener, pageIndexs);
		});
	}

	/**
	 * 拆分 PDF 文档为单页文件
	 * <p>
	 * 委托 {@link PDFSplitter} 执行拆分逻辑，将当前文档的每一页拆分为独立的 PDF 文件，返回所有拆分后的文件列表。
	 * 若文档实例为空，返回空列表；拆分后的文件存储路径、命名规则由 {@link PDFSplitter} 定义（可通过其配置扩展）。
	 * </p>
	 *
	 * @return List&lt;File&gt; - 拆分后的单页 PDF 文件列表（若拆分失败或文档为空，返回空列表）
	 * @throws IOException 拆分过程中发生的 IO 异常（如文档损坏、存储路径无写入权限等）
	 */
	public List<File> split() throws IOException {
		return autoCloseable().map((document) -> {
			PDFSplitter pdfSplitter = new PDFSplitter();
			PDFUtils.read(document, pdfSplitter);
			return pdfSplitter.getOutputFiles();
		}).get();
	}

	/**
	 * 拆分 PDF 文档的指定页面为独立文件
	 * <p>
	 * 重载 split 方法，支持指定具体页面索引（索引从 0 开始），仅拆分目标页面为独立 PDF 文件，提升拆分效率。 委托
	 * {@link PDFSplitter} 执行定向拆分逻辑，返回拆分后的目标页面文件列表。
	 * </p>
	 *
	 * @param pageIndexs 目标页面索引数组（非空校验：避免空指针；有效范围：0 ≤ 索引 &lt; 文档总页数，超出范围的索引会被忽略）
	 * @return List&lt;File&gt; - 拆分后的指定页面 PDF 文件列表（若拆分失败、文档为空或无有效索引，返回空列表）
	 * @throws IOException 拆分过程中发生的 IO 异常（如文档损坏、存储路径无写入权限等）
	 */
	public List<File> split(@NonNull int... pageIndexs) throws IOException {
		return autoCloseable().map((document) -> {
			PDFSplitter pdfSplitter = new PDFSplitter();
			PDFUtils.read(document, pdfSplitter, pageIndexs);
			return pdfSplitter.getOutputFiles();
		}).get();
	}
}