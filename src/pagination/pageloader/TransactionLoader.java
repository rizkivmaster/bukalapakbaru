package pagination.pageloader;

import pagination.PageLoader;
import pagination.UpdateableAdapter;
import model.business.Credential;
import model.business.Transaction;
import model.session.LogicalComparator;
import model.session.PageConsistency;
import android.content.Context;
import api.ListTransaction;
import api.ReadTransaction;

public class TransactionLoader extends PageLoader<Transaction> {
	public TransactionLoader(UpdateableAdapter<Transaction> a,Context con,Credential c) {
		super(a,c);
		setPaginator(new ListTransaction(con, c));
		setRetriever(new ReadTransaction(con, c, null));
		setLogicalComparator(new LogicalComparator<Transaction>() {
			@Override
			public boolean logicallyEquals(Transaction arg0, Transaction arg1) {
				return arg0.getId().equals(arg1.getId());
			}
		});
		setPageConsistency(PageConsistency.PERSISTENT);
	}
}
