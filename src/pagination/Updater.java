package pagination;

//update whatever you want
public interface Updater<T> {
	public abstract void update(T newElement,T oldElement);
	public abstract boolean isOutdated(T newElement,T oldElement);
}
