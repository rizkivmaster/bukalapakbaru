package pagination.pageloader;

import pagination.PageLoader;
import pagination.UpdateableAdapter;
import model.business.Credential;
import model.business.SoldProduct;
import model.session.PageConsistency;
import android.content.Context;
import api.ListLapakTidakDijual;

public class LapakTidakDijualLoader extends PageLoader<SoldProduct> {
	public LapakTidakDijualLoader(UpdateableAdapter<SoldProduct> a,Context con,Credential c) {
		super(a,c);
		setPaginator(new ListLapakTidakDijual(con, c));
		setPageConsistency(PageConsistency.NOT_PERSISTENT);
	}
}
