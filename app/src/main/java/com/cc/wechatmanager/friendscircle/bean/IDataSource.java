package com.cc.wechatmanager.friendscircle.bean;

import java.util.List;

public interface IDataSource {

	String getUsername();

	long getDate();

	int getmLogo();

	int[] getZanLogos();

	int getZanCount();

	List<CommonBean> getCommonBeans();

	int getCommonCount();
	
}
