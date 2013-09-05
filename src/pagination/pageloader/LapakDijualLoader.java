package pagination.pageloader;

import pagination.PageLoader;
import pagination.UpdateableAdapter;
import model.business.AvailableProduct;
import model.business.Credential;
import model.business.Transaction;
import model.session.LogicalComparator;
import model.session.PageConsistency;
import android.content.Context;
import api.ListLapakDijual;
import api.ListTransaction;
import api.ReadTransaction;

public class LapakDijualLoader extends PageLoader<AvailableProduct> {
	public LapakDijualLoader(UpdateableAdapter<AvailableProduct> a,Context con,Credential c) {
		super(a,c);
		setPaginator(new ListLapakDijual(con, c));
		setPageConsistency(PageConsistency.NOT_PERSISTENT);
	}
}
