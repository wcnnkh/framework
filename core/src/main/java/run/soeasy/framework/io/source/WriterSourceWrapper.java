package run.soeasy.framework.io.source;

import run.soeasy.framework.core.domain.Wrapper;

import java.io.IOException;
import java.io.Writer;

public interface WriterSourceWrapper<T extends Writer, W extends WriterSource<T>> extends WriterSource<T>, Wrapper<W> {
    @Override
    default T getWriter() throws IOException{
        return getSource().getWriter();
    }
}
