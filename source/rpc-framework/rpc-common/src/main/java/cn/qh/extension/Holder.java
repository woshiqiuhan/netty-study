package cn.qh.extension;

public class Holder<T> {
    private volatile T data;

    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }
}
