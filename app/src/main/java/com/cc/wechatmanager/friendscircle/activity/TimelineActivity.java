package com.cc.wechatmanager.friendscircle.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.wechatmanager.R;
import com.cc.wechatmanager.friendscircle.entity.FriendEntity;
import com.cc.wechatmanager.friendscircle.util.MyListView;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.LoginUserResult;
import com.cc.wechatmanager.model.SnsListResult;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TimelineActivity extends Activity {
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;
	public static final int PHOTOZOOM = 2;
	public static final int PHOTORESOULT = 3;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final String TEMP_JPG_NAME = "temp.jpg";
	private MyListView listView;
	private MyAdapter adapter;
	private LinearLayout relativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sns);
		relativeLayout = (LinearLayout) findViewById(R.id.RelativeLayout);
		//listView = (ListView) findViewById(R.id.listview);
		ArrayList<String> list = new ArrayList<String>();
		listView = new MyListView(this, list);
		relativeLayout.addView(listView);
        adapter = new MyAdapter(null, this);
		listView.setAdapter(adapter);
        getLoginUser();
        getContactList();
        getSnsList();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {

			if (resultCode == NONE)
				return;

			if (requestCode == PHOTOHRAPH) {
				File picture = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath(),
						TEMP_JPG_NAME);
				startPhotoZoom(Uri.fromFile(picture));
			}

			if (data == null)
				return;

			if (requestCode == PHOTOZOOM) {
				/*
				 * Uri image = data.getData();
				 * Toast.makeText(MymessageActivity.this,image+"",
				 * Toast.LENGTH_LONG).show();
				 */

				if (data != null) {
					startPhotoZoom(data.getData());
				} else {
					System.out.println("================");
				}

			}

			if (requestCode == PHOTORESOULT) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					// Toast.makeText(TimelineActivity.this,Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartTableLamp/",
					// Toast.LENGTH_LONG).show();
					Bitmap photo = extras.getParcelable("data");

					listView.setAdapter(new MyAdapter(null, this));
					// ByteArrayOutputStream stream = new
					// ByteArrayOutputStream();
					// comp(photo);
					// photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					// riv_logo.setImageBitmap(photo);
					// SelectPicUtil.bitmapToBase64(photo,
					// MymessageActivity.this);
					/*
					 * logoName =
					 * FileUtils.getFilename(MainAppUtil.getCustom().getSusername
					 * ()); FileUtils.writeFile(Constants.LOGO_CACHE_PATH,
					 * logoName, photo);
					 */
				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();//
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;//
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	private Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 500f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);
	}

	private void getLoginUser() {
		Messenger.Companion.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
			@Override
			public void onResult(@Nullable String result) {
				final LoginUserResult result1 = StrUtils.fromJson(result, LoginUserResult.class);
				if (result1 == null) {
					return;
				}
				if (!result1.isSuccess()) {
					Utils.showToast("无法获取用户信息");
					KLog.e("can not get login user, " + result1.getMessage());
				} else {
				    listView.post(new Runnable() {
                        @Override
                        public void run() {

                            listView.updateUserInfo(result1.getData());
                        }
                    });
				}
			}
		});
	}

	private void getContactList() {
        Messenger.Companion.sendCommand(genCommand("getContacts"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                final ContactsResult result1 = StrUtils.fromJson(result, ContactsResult.class);
                if (result1 == null) {
                    return;
                }
                if (!result1.isSuccess()) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                } else {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter.setContacts(result1.getData());
                        }
                    });
                }
            }
        });
    }

    private void getSnsList() {

        Messenger.Companion.sendCommand(genCommand("getSnsList", 0), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                final SnsListResult result1 = StrUtils.fromJson(result, SnsListResult.class);
                if (result1 == null) {
                    return;
                }
                if (!result1.isSuccess()) {
                    KLog.e("can not get sns list, " + result1.getMessage());
                } else {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter.setSnsInfoList(result1.getData());
                        }
                    });
                }
            }
        });
    }

	public static Command genCommand(String key, Object... data) {
		Command c = new Command();
		c.setKey(key);
		c.setId(UUID.randomUUID().toString());
		List<Object> args = new ArrayList<>(Arrays.asList(data));
		c.setArgs(args);
		return c;
	}
}
