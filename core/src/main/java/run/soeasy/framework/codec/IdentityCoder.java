package run.soeasy.framework.codec;

enum IdentityCoder implements UnaryCoder<Object> {
    INSTANCE;

    @Override
    public Object apply(Object t) {
        return t;
    }
}