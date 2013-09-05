package bukalapak.view.profile;

import java.io.IOException;
import java.io.StreamCorruptedException;

import profiling.ProfileManager;
import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import model.business.User;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import api.ReadUserInfo;

import com.bukalapakdummy.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ProfilFragment extends Fragment {
	String pesan;
	ImageView avatarUser;
	TextView nameUser;
	TextView emailUser;
	TextView phoneUser;
	TextView bankUser;
	TextView rekUser;
	Credential credential;
	Context context;
	
	ScrollView lv;
	private DisplayImageOptions options;
	
	public ProfilFragment() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
		.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(50))
		.build();
	}
	@SuppressLint("ValidFragment")
	public ProfilFragment(String p) {
		// TODO Auto-generated constructor stub
		pesan = p;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lv = (ScrollView) inflater.inflate(R.layout.profil, null);		
		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		
		avatarUser = (ImageView) lv.findViewById(R.id.prof_pic);
		nameUser = (TextView) lv.findViewById(R.id.prof_nama);
		emailUser = (TextView) lv.findViewById(R.id.prof_email);
		phoneUser = (TextView) lv.findViewById(R.id.prof_phone);
		bankUser = (TextView) lv.findViewById(R.id.prof_bankName);
		rekUser = (TextView) lv.findViewById(R.id.prof_bankRek);
		
		prepareProfile();
		
		return lv;
		
	}
	
	private void prepareProfile()
	{
		ProfileManager manager = new ProfileManager(credential);
		User user = null;
		try {
			user = manager.load();
		} catch (StreamCorruptedException e) {
			downloadProfile();
		} catch (IOException e) {
			downloadProfile();
		} catch (ClassNotFoundException e) {
			downloadProfile();
		}
		if(user!=null)
		{
			loadProfile(user);
		}
		else
		{
			downloadProfile();
		}
	}
	
	private void downloadProfile()
	{
		ReadUserInfo task = new ReadUserInfo(context, credential);
		task.setAPIListener(new APIListener<User>() {
			ProgressDialog pd;
			@Override
			public void onSuccess(User user) {
				pd.dismiss();
				ProfileManager manager = new ProfileManager(credential);
				try {
					manager.save(user);
				} catch (IOException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				loadProfile(user);
			}
			
			@Override
			public void onFailure(Exception e) {
				pd.dismiss();
			}
			
			@Override
			public void onExecute() {
				pd= new ProgressDialog(context);
				pd.setTitle("Profil");
				pd.setMessage("Tunggu sebentar, sedang mengunduh profil...");
				pd.setIndeterminate(true);
				pd.setCancelable(true);
				pd.show();
			}
		});
		task.execute();
	}
	
	private void loadProfile(User user)
	{
		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageSize targetSize = new ImageSize(100, 100); // result Bitmap will be fit to this size
		imageLoader.loadImage("https://www.bukalapak.com/"+user.getAvatar(), targetSize, options, new SimpleImageLoadingListener() {
		    @Override
		    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		    	avatarUser.setImageBitmap(loadedImage);
		    }
		});
		if(user.getName()!=null && !user.getName().isEmpty())nameUser.setText(user.getName());
		else nameUser.setText("<tidak tersedia>");
		if(user.getEmail()!=null && !user.getEmail().isEmpty())emailUser.setText(user.getEmail());
		else emailUser.setText("<tidak tersedia>");
		if(user.getPhone()!=null && !user.getPhone().isEmpty())phoneUser.setText(user.getPhone());
		else phoneUser.setText("<tidak tersedia>");
		if(user.getBankName()!=null && !user.getBankName().isEmpty())bankUser.setText(user.getBankName());
		else bankUser.setText("<tidak tersedia>");
		if(user.getBankNumber()!=null && !user.getBankNumber().isEmpty())rekUser.setText(user.getBankNumber());
		else rekUser.setText("<tidak tersedia>");
	}

}
