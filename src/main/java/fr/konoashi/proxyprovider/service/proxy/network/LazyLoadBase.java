package fr.konoashi.proxyprovider.service.proxy.network;

public abstract class LazyLoadBase<T> {
    private T value;
    private boolean isLoaded = false;

    public LazyLoadBase() {
    }

    public T getValue() {
        if (!this.isLoaded) {
            this.isLoaded = true;
            this.value = this.load();
        }

        return this.value;
    }

    protected abstract T load();
}
