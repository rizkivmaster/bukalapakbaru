package pagination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import listener.APIListener;
import model.business.Credential;
import model.session.LogicalComparator;
import model.session.PageConsistency;
import model.session.Paginator;
import model.session.Retriever;
import android.os.Environment;
import android.widget.Filterable;
import exception.ProductNotFoundException;

public class PageLoader<T extends Serializable> {
	private PageLoaderListener refreshListener;
	private PageLoaderListener loadMoreListener;
	private UpdateableAdapter<T> adapter;
	private Paginator<T> paginator;
	private Retriever<T> retriever;
	private LogicalComparator<T> comparator;
	private final int PAGE_SIZE = 10;
	private PageConsistency pageConsistency;
	private Credential credential;

	public PageLoader(UpdateableAdapter<T> a, Credential c) {
		adapter = a;
		pageConsistency = PageConsistency.PERSISTENT;
		credential = c;
	}

	protected void setPageConsistency(PageConsistency pc) {
		pageConsistency = pc;
	}

	protected void setPaginator(Paginator<T> pg) {
		paginator = pg;
	}

	protected void setRetriever(Retriever<T> r) {
		retriever = r;
	}

	protected void setLogicalComparator(LogicalComparator<T> lc) {
		comparator = lc;
	}

	private int countLastPageIndex() {
		int result = ((adapter.getElements().size() + PAGE_SIZE - 1) / PAGE_SIZE);
		return result;
	}

	private void saveLocalPage() {
		FileOutputStream fout;
		try {
			String signature = this.getClass().getName();
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/bukalapak/"
					+ credential.getUserid()
					+ "/cache/");
			folder.mkdirs();
			File file = new File(folder, signature);
			if (file.exists())
				file.delete();
			file.createNewFile();
			fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(adapter.getElements());
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<T> loadLocalPage() throws StreamCorruptedException,
			IOException, ClassNotFoundException {
		String signature = this.getClass().getName();
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/bukalapak/"
				+ credential.getUserid()
				+ "/cache/" + signature);
		ArrayList<T> result = new ArrayList<T>();
		if (file.exists()) {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream iis = new ObjectInputStream(fin);
			result.addAll((ArrayList<T>) iis.readObject());
		}
		return result;
	}

	public void initializePage() {
		try {
			ArrayList<T> list = loadLocalPage();
			adapter.setElements(list);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PageLoaderListener getRefreshListener() {
		return refreshListener;
	}

	public void setRefreshListener(PageLoaderListener listener) {
		this.refreshListener = listener;
	}

	public PageLoaderListener getLoadMoreListener() {
		return loadMoreListener;
	}

	public void setLoadMoreListener(PageLoaderListener listener) {
		this.loadMoreListener = listener;
	}

	protected void tellRefreshSuccess() {
		saveLocalPage();
		if (refreshListener != null)
			refreshListener.onSuccess(true);
	}

	protected void tellRefreshFailure(Exception e) {
		if (refreshListener != null)
			refreshListener.onFailure(e);
	}

	protected void tellRefreshStart() {
		if (refreshListener != null)
			refreshListener.onStart();
	}

	protected void tellLoadMoreSuccess(boolean isAtDown) {
		saveLocalPage();
		if (loadMoreListener != null)
			loadMoreListener.onSuccess(isAtDown);
	}

	protected void tellLoadMoreFailure(Exception e) {
		if (loadMoreListener != null)
			loadMoreListener.onFailure(e);
	}

	protected void tellLoadMoreStart() {
		if (loadMoreListener != null)
			loadMoreListener.onStart();
	}

	public void refreshPage() {
		PageLoaderListener listener = new PageLoaderListener() {

			@Override
			public void onSuccess(boolean isAtDown) {
				tellRefreshSuccess();
			}

			@Override
			public void onStart() {
				tellRefreshStart();
			}

			@Override
			public void onFailure(Exception e) {
				tellRefreshFailure(e);
			}
		};
		refresh(listener);
	}

	@SuppressWarnings("unchecked")
	private void refresh(PageLoaderListener listener) {
		ArrayList<T> tobeRefreshed = new ArrayList<T>();
		tobeRefreshed.addAll(adapter.getElements());
		if (tobeRefreshed.size() > 0) {
			if (pageConsistency == PageConsistency.PERSISTENT) {
				if (listener != null)
					listener.onStart();
				refreshLoop(
						adapter.getElements().get(
								adapter.getElements().size() - 1), listener);
			} else if (pageConsistency == PageConsistency.NOT_PERSISTENT) {
				if (retriever != null && comparator != null) {
					setUpLastElement(listener);
				} else {
					refreshPrimitive(listener);
				}
			}
		}
	}

	private void setUpLastElement(final PageLoaderListener listener) {
		setUpLastElement(adapter.getElements().size() - 1, listener);
	}

	private void setUpLastElement(final int position,
			final PageLoaderListener listener) {
		T lastElement = adapter.getElements().get(
				adapter.getElements().size() - 1);
		retriever.setRetrievable(lastElement);
		retriever.setRetrieverListener(new APIListener<T>() {

			@Override
			public void onExecute() {
				if (listener != null)
					listener.onStart();
			}

			@Override
			public void onSuccess(T res) {
				refreshLoop(res, listener);
			}

			@Override
			public void onFailure(Exception e) {
				if (e instanceof ProductNotFoundException) {
					if (position - 1 > 0) {
						setUpLastElement(position - 1, listener);
					} else {
						loadMorePage();
					}
				} else {
					if (listener != null)
						listener.onFailure(e);
				}
			}
		});
		retriever.executeRetrieval();
	}

	private void refreshLoop(T lastElement, final PageLoaderListener listener) {
		ArrayList<T> newList = new ArrayList<T>();
		refreshLoop(lastElement, 1, listener, newList);
	}

	private void refreshLoop(final T lastElement, final int index,
			final PageLoaderListener listener, final ArrayList<T> resultList) {
		paginator.setPage(index, PAGE_SIZE);
		paginator.setPaginatorListener(new APIListener<ArrayList<T>>() {

			@Override
			public void onExecute() {
			}

			@Override
			public void onSuccess(ArrayList<T> res) {
				boolean isFound = false;
				for (int ii = 0; ii < res.size(); ii++) {
					resultList.add(res.get(ii));
					if (comparator.logicallyEquals(res.get(ii), lastElement)) {
						isFound = true;
						adapter.setElements(resultList);
						if (listener != null)
							listener.onSuccess(true);
						break;
					}
				}
				if (!isFound) {
					refreshLoop(lastElement, index + 1, listener, resultList);
				}
			}

			@Override
			public void onFailure(Exception e) {
				if (listener != null)
					listener.onFailure(e);
			}
		});
		paginator.executePaging();
	}

	private void refreshPrimitive(final PageLoaderListener listener) {
		int lastPage = countLastPageIndex();
		int totalPageSize = lastPage * PAGE_SIZE;
		paginator.setPage(1, totalPageSize);
		paginator.setPaginatorListener(new APIListener<ArrayList<T>>() {

			@Override
			public void onExecute() {
				if (listener != null)
					listener.onStart();
			}

			@Override
			public void onSuccess(ArrayList<T> res) {
				adapter.setElements(res);
				if (listener != null)
					listener.onSuccess(true);
			}

			@Override
			public void onFailure(Exception e) {
				if (listener != null)
					listener.onFailure(e);
			}
		});
		paginator.executePaging();
	}

	public void loadMorePage() {

		if (adapter.getElements().size() > 0) {
			if (pageConsistency == PageConsistency.PERSISTENT) {
				tellLoadMoreStart();
				loadDown();
			} else if (pageConsistency == PageConsistency.NOT_PERSISTENT) {
				PageLoaderListener pageListener = new PageLoaderListener() {
					@Override
					public void onSuccess(boolean isAtEnd) {
						loadDown();
					}

					@Override
					public void onStart() {
						tellLoadMoreStart();
					}

					@Override
					public void onFailure(Exception e) {
						tellLoadMoreFailure(e);
					}
				};
				refresh(pageListener);
			}
		} else {
			final APIListener<ArrayList<T>> renewListener = new APIListener<ArrayList<T>>() {

				@Override
				public void onExecute() {
					tellLoadMoreStart();
				}

				@Override
				public void onSuccess(ArrayList<T> res) {
					adapter.setElements(res);
					if (res.size() == PAGE_SIZE) {
						tellLoadMoreSuccess(false);
					} else {
						tellLoadMoreSuccess(true);
					}
				}

				@Override
				public void onFailure(Exception e) {
					tellLoadMoreFailure(e);
				}
			};
			loadPage(renewListener, 1);
		}
	}

	private void loadPage(APIListener<ArrayList<T>> listener, int index) {
		paginator.setPage(index, PAGE_SIZE);
		paginator.setPaginatorListener(listener);
		paginator.executePaging();
	}

	private void loadDown() {
		final APIListener<ArrayList<T>> loadDownListener = new APIListener<ArrayList<T>>() {

			@Override
			public void onExecute() {
			}

			@Override
			public void onSuccess(ArrayList<T> res) {
				ArrayList<T> newList = new ArrayList<T>();
				newList.addAll(adapter.getElements());
				newList.addAll(res);
				adapter.setElements(newList);
				if (res.size() == PAGE_SIZE) {
					tellLoadMoreSuccess(false);
				} else {
					tellLoadMoreSuccess(true);
				}
			}

			@Override
			public void onFailure(Exception e) {
				tellLoadMoreFailure(e);
			}
		};
		final APIListener<ArrayList<T>> remainderListener = new APIListener<ArrayList<T>>() {

			@Override
			public void onExecute() {
			}

			@Override
			public void onSuccess(ArrayList<T> res) {
				int offset = (adapter.getElements().size() - 1) % PAGE_SIZE;
				for (int ii = offset + 1; ii < res.size(); ii++) {
					adapter.getElements().add(res.get(ii));
				}
				int lastIndex = countLastPageIndex();
				loadPage(loadDownListener, lastIndex + 1);
			}

			@Override
			public void onFailure(Exception e) {
				tellLoadMoreFailure(e);
			}
		};

		int lastIndex = countLastPageIndex();
		if (adapter.getElements().size() % PAGE_SIZE == 0) {
			loadPage(loadDownListener, lastIndex + 1);
		} else {
			loadPage(remainderListener, lastIndex);
		}
	}

	public void closePage() {
		saveLocalPage();
	}
}
