package pagination;


public interface PageLoaderListener {
	public abstract void onSuccess(boolean isEnd);
	public abstract void onFailure(Exception e);
	public abstract void onStart();
}
