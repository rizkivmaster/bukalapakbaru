package pagination.pageloader;

import pagination.PageLoader;
import pagination.UpdateableAdapter;
import model.business.Credential;
import model.business.MessageLine;
import model.business.Transaction;
import model.session.LogicalComparator;
import model.session.PageConsistency;
import android.content.Context;
import api.GetInbox;
import api.ListTransaction;
import api.ReadTransaction;

public class InboxLoader extends PageLoader<MessageLine> {
	public InboxLoader(UpdateableAdapter<MessageLine> a, Context con,
			Credential c) {
		super(a, c);
		setPaginator(new GetInbox(con, c));
		LogicalComparator<MessageLine> comparator = new LogicalComparator<MessageLine>() {
			
			@Override
			public boolean logicallyEquals(MessageLine one, MessageLine other) {
				return one.getId().equals(other.getId());
			}
		};
		setLogicalComparator(comparator);
		setPageConsistency(PageConsistency.NOT_PERSISTENT);
	}
}
