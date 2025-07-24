package run.soeasy.framework.codec.binary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 传输器编解码器，实现{@link BinaryCodec}接口，通过组合编码传输器和解码传输器，
 * 提供二进制数据的编解码能力，适用于将编码与解码的传输逻辑分离的场景。
 * 
 * <p>该类将编码过程委托给编码传输器（{@link #encodeTransferrer}），
 * 解码过程委托给解码传输器（{@link #decodeTransferrer}），
 * 实现了编解码器与传输器的解耦，便于灵活组合不同的传输逻辑。
 * 
 * @param <E> 编码用传输器类型（需实现{@link BinaryTransferrer}）
 * @param <D> 解码用传输器类型（需实现{@link BinaryTransferrer}）
 * @author soeasy.run
 * @see BinaryCodec
 * @see BinaryTransferrer
 */
@RequiredArgsConstructor
@Getter
public class TransferrerCodec<E extends BinaryTransferrer, D extends BinaryTransferrer> implements BinaryCodec {

    /**
     * 用于编码过程的传输器，负责处理编码时的数据传输逻辑
     */
    private final E encodeTransferrer;

    /**
     * 用于解码过程的传输器，负责处理解码时的数据传输逻辑
     */
    private final D decodeTransferrer;
}