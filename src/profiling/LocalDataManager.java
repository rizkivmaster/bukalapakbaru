package profiling;

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

import model.business.Credential;
import android.os.Environment;

public class LocalDataManager<T extends Serializable> {
	private Credential credential;
	private String suffix;

	public LocalDataManager(Credential c, String s) {
		credential = c;
		suffix = s;
	}

	protected void save(T obj, String name) throws IOException {
		FileOutputStream fout;
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/bukalapak/"
				+ credential.getUserid()
				+ "/" + suffix);
		folder.mkdirs();
		File file = new File(folder, name);
		if (file.exists())
			file.delete();
		file.createNewFile();
		fout = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(obj);
		oos.close();
	}

	protected T load(String name) throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/bukalapak/"
				+ credential.getUserid()
				+ "/" + suffix + name);
		T result = null;
		if (file.exists()) {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream iis = new ObjectInputStream(fin);
			result = (T) iis.readObject();
		}
		return result;
	}
	
	protected void delete(String name)
	{
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/bukalapak/"
				+ credential.getUserid()
				+ "/" + suffix + name);
		if (file.exists()) {
			file.delete();
		}
	}
}
