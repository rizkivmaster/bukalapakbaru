package updater;

import pagination.Updater;
import model.business.Transaction;

public class TransactionUpdater implements Updater<Transaction>  {
	@Override
	public void update(Transaction newTransaction, Transaction oldTransaction) {
		oldTransaction.setState(newTransaction.getState());
	}

	@Override
	public boolean isOutdated(Transaction newElement, Transaction oldElement) {
		return newElement.getState()!=oldElement.getState();
	}
}
