package profiling;

import java.io.IOException;
import java.io.StreamCorruptedException;

import model.business.Credential;
import model.business.User;

public class ProfileManager extends LocalDataManager<User> {

	public ProfileManager(Credential c) {
		super(c, "");
	}
	
	public User load() throws StreamCorruptedException, IOException, ClassNotFoundException {
		return super.load("profile");
	}
	
	public void save(User user) throws IOException
	{
		super.save(user, "profile");
	}
	
	public void delete()
	{
		super.delete("profile");
	}
}
