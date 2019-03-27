package com.ionesmile.datasource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T> extends BaseAdapter{

	protected List<T> data;
	protected Context context;
	protected LayoutInflater inflater;
	
	public MyBaseAdapter(Context context, List<T> data) {
		this.data = data;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	

	public void setData(List<T> data){
		this.data = data;
	}

	public void setDataAndNotify(List<T> data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	/**
	 * ������ݼ���
	 * @param data
	 */
	public void addDataAll(List<T> data){
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	public void addData(T item){ 
		this.data.add(item);
		notifyDataSetChanged();
	}

	public void clearData(){
		this.data.clear();
		notifyDataSetChanged();
	}
	
	/**
	 * ����
	 * @param comparator	ʵ����Comparator�ӿڵĶ���
	 */
	public void sort(Comparator<? super T> comparator){
		Collections.sort(data, comparator);
	}
	
	@Override
	public int getCount() {
		if (data == null) {
			return 0;
		}
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
