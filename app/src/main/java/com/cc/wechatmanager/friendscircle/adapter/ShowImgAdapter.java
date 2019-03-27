package com.cc.wechatmanager.friendscircle.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cc.wechatmanager.R;
import com.loopj.android.image.SmartImageView;

public class ShowImgAdapter extends BaseAdapter {

	private ArrayList<String> arrayList;
	private Context context;

	public ShowImgAdapter(Context context, ArrayList<String> arrayList) {
		this.arrayList = arrayList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.showimg_adapter, null);
			holder.imageView = (SmartImageView) convertView
					.findViewById(R.id.show_imgview);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		String path = arrayList.get(position);
		Bitmap bitmap = getLoacalBitmap(path);
		holder.imageView.setImageBitmap(bitmap);
		return convertView;
	}

	private class Holder {
		public SmartImageView imageView;
	}


	public static Bitmap getLoacalBitmap(String fileName) {
		
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds =false;
            options.inSampleSize = 4;
            Bitmap b = BitmapFactory.decodeFile(fileName, options); 
            return b;
	}
}
