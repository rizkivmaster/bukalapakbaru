package pagination.pageloader;

import pagination.PageLoader;
import pagination.UpdateableAdapter;
import model.business.Credential;
import model.business.Negotiation;
import model.business.Transaction;
import model.session.LogicalComparator;
import model.session.PageConsistency;
import android.content.Context;
import api.ListNegotiations;
import api.ListTransaction;
import api.ReadTransaction;

public class NegotiationLoader extends PageLoader<Negotiation> {
	public NegotiationLoader(UpdateableAdapter<Negotiation> a,Context con,Credential c) {
		super(a,c);
		setPaginator(new ListNegotiations(con, c));
		LogicalComparator<Negotiation> comparator = new LogicalComparator<Negotiation>() {
			
			@Override
			public boolean logicallyEquals(Negotiation one, Negotiation other) {
				return one.getID().equals(other.getID());
			}
		};
		setLogicalComparator(comparator);
		setPageConsistency(PageConsistency.PERSISTENT);
	}
}
