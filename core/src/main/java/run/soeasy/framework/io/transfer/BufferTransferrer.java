package run.soeasy.framework.io.transfer;

import lombok.NonNull;

import java.io.IOException;
import java.nio.Buffer;

/**
 * Transfers data from a {@link BufferReader} to a {@link BufferWriter}.
 *
 * <p>This interface does NOT guarantee thread safety.
 *
 * <p><b>Contract:</b>
 * <ul>
 *   <li>This method is responsible for loop control (read → write)</li>
 *   <li>Implementations must fully drain the reader or handle errors</li>
 *   <li>Exceptions from either side must be propagated</li>
 * </ul>
 *
 * <p>This interface is a {@link FunctionalInterface} and can be used as a lambda target.
 *
 * @param <B> the type of buffer being transferred
 */
@FunctionalInterface
public interface BufferTransferrer<B extends Buffer> {

    /**
     * Core transfer logic.
     *
     * <p>Implementations should repeatedly read from the reader and write to the writer
     * until the reader is exhausted or an exception occurs.
     *
     * @param reader the source of data
     * @param writer the destination of data
     * @throws IOException if an I/O error occurs during transfer
     */
    void transfer(
            @NonNull BufferReader<? super B> reader,
            @NonNull BufferWriter<? super B> writer
    ) throws IOException;
}