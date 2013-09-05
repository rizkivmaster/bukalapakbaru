package pagination;

import java.util.ArrayList;

public interface UpdateableAdapter<T> {

	public abstract void setElements(ArrayList<T> list);
	
	public abstract ArrayList<T> getElements();
}
