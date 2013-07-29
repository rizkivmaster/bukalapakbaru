package com.example.bukalapakdummy;

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
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "Profil", Toast.LENGTH_SHORT).show();
		lv = (ScrollView) inflater.inflate(R.layout.profil, null);
		
		context = getActivity();
		credential = CredentialEditor.loadCredential(context);
		
		avatarUser = (ImageView) lv.findViewById(R.id.prof_pic);
		nameUser = (TextView) lv.findViewById(R.id.prof_nama);
		emailUser = (TextView) lv.findViewById(R.id.prof_email);
		phoneUser = (TextView) lv.findViewById(R.id.prof_phone);
		bankUser = (TextView) lv.findViewById(R.id.prof_bankName);
		rekUser = (TextView) lv.findViewById(R.id.prof_bankRek);
		
		ReadUserInfo task = new ReadUserInfo(context, credential);
		task.setAPIListener(new APIListener<User>() {
			ProgressDialog pd;
			@Override
			public void onSuccess(User res) {
				pd.dismiss();
				ImageLoader imageLoader = ImageLoader.getInstance();
				ImageSize targetSize = new ImageSize(100, 100); // result Bitmap will be fit to this size
				imageLoader.loadImage("https://www.bukalapak.com/"+res.getAvatar(), targetSize, options, new SimpleImageLoadingListener() {
				    @Override
				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				    	avatarUser.setImageBitmap(loadedImage);
				    }
				});
				nameUser.setText(res.getName());
				emailUser.setText(res.getEmail());
				phoneUser.setText(res.getPhone());
				bankUser.setText(res.getBankName());
				rekUser.setText(res.getBankNumber());
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
		
		return lv;
		
	}

}
