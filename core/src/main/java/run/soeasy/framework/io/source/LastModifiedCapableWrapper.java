package run.soeasy.framework.io.source;

import run.soeasy.framework.core.domain.Wrapper;

import java.io.IOException;

public interface LastModifiedCapableWrapper<W extends LastModifiedCapable> extends  LastModifiedCapable, Wrapper<W> {

    @Override
    default long lastModified() throws IOException{
        return getSource().lastModified();
    }
}
