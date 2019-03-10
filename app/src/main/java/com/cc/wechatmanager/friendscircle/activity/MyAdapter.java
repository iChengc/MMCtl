package com.cc.wechatmanager.friendscircle.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cc.core.command.Callback;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.model.sns.SnsComment;
import com.cc.core.wechat.model.sns.SnsCommentRequest;
import com.cc.core.wechat.model.sns.SnsInfo;
import com.cc.core.wechat.model.sns.SnsLike;
import com.cc.core.wechat.model.user.Friend;
import com.cc.wechatmanager.MainActivity;
import com.cc.wechatmanager.R;
import com.cc.wechatmanager.friendscircle.adapter.ImageAdapter;
import com.cc.wechatmanager.friendscircle.dialog.ActionSheetDialog;
import com.cc.wechatmanager.model.CommandResult;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<SnsInfo> data = new ArrayList<>();
    private Context context;
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;
    public static final int PHOTOZOOM = 2;
    public static final int PHOTORESOULT = 3;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String TEMP_JPG_NAME = "temp.jpg";
    private List<Friend> contacts;

    public MyAdapter(List<SnsInfo> list, Context context) {
        if (list != null) {
            data.addAll(list);
        }
        contacts = new ArrayList<>();
        this.context = context;
    }

    public void setSnsInfoList(List<SnsInfo> infoList) {
        if (infoList != null) {
            data.addAll(infoList);
        }
        notifyDataSetChanged();
    }

    public void setContacts(List<Friend> friends) {
        contacts.clear();
        if (friends != null) {
            contacts.addAll(friends);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public SnsInfo getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        SnsInfo info = getItem(position);
        if (info.getSnsType() == SnsInfo.TEXT_TYPE
                || info.getSnsType() == SnsInfo.IMAGE_TYPE
                || info.getSnsType() == SnsInfo.VIDEO_TYPE) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final SnsInfo info = getItem(position);
        if (info.getSnsType() == SnsInfo.TEXT_TYPE
                || info.getSnsType() == SnsInfo.IMAGE_TYPE
                || info.getSnsType() == SnsInfo.VIDEO_TYPE) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.circle_item, null);
                holder = new ViewHolder();
                holder.gridView = convertView.findViewById(R.id.gridView);
                holder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

                holder.content = convertView.findViewById(R.id.content);
                holder.commentary = convertView.findViewById(R.id.commentary);
                holder.date = convertView.findViewById(R.id.publish_time);
                holder.name = convertView.findViewById(R.id.name);
                holder.praise = convertView.findViewById(R.id.praise);
                holder.images = convertView.findViewById(R.id.images);
                holder.contentimage = convertView.findViewById(R.id.contentimage);
                holder.imageBtn = convertView.findViewById(R.id.imgButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //commentSns(info, "comment 测试", null);
                    commentSnsCancel(info, null);
                }
            });

            if (info.getMedias() == null || info.getMedias().isEmpty()) {
                holder.gridView.setVisibility(View.GONE);
            } else {
                holder.gridView.setVisibility(View.VISIBLE);
                switch (info.getMedias().size()) {
                    case 1:
                        holder.gridView.setNumColumns(1);
                    case 2:
                        holder.gridView.setNumColumns(2);
                    default:
                        holder.gridView.setNumColumns(3);

                }
                holder.gridView.setAdapter(new ImageAdapter(context, info.getMedias()));
            }

           /* holder.gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1,
                                        int positions, long arg3) {
					*//*if(position ==0){
						holdergridview.gridView.setAdapter(new ImageAdapter(context,3,R.drawable.w002,positions,R.drawable.w003));
					}*//*
                    showLogoSwitchWindow();
                }
            });*/
            holder.content.setText(info.getDescription());
            holder.commentary.setText("ooooooojjjjjjjj");
            holder.date.setText(SimpleDateFormat.getInstance().format(new Date(info.getCreateTime() * 1000)));
            holder.name.setText(getFriendNickNameById(info.getUserName()));
            holder.praise.setText(getLikeText(info));
            String avatar = getFriendAvatarById(info.getUserName());
            if (!TextUtils.isEmpty(avatar)) {
                Glide.with(context).load(avatar).into(holder.images);
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.otherlayout_item, null);
                holder = new ViewHolder();
                holder.gridView = convertView.findViewById(R.id.gridView);
                holder.content = convertView.findViewById(R.id.content);
                holder.commentary = convertView.findViewById(R.id.commentary);
                holder.praise = convertView.findViewById(R.id.praise);
                holder.date = convertView.findViewById(R.id.publish_time);
                holder.name = convertView.findViewById(R.id.name);
                holder.contentimage = convertView.findViewById(R.id.contentimage);
                holder.images = convertView.findViewById(R.id.images);
                holder.imageBtn = convertView.findViewById(R.id.imgButton);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.content.setText(info.getDescription());
            holder.commentary.setText("comment:rnext.hahah\nlojhgj:ogijjebgu");
            holder.date.setText(SimpleDateFormat.getInstance().format(new Date(info.getCreateTime() * 1000)));
            holder.name.setText(getFriendNickNameById(info.getUserName()));
            if (info.getMedias() != null && info.getMedias().size() > 0) {
                Glide.with(context).load(info.getMedias().get(0)).into(holder.contentimage);
            }
            holder.praise.setText(getLikeText(info));
            String avatar = getFriendAvatarById(info.getUserName());
            if (!TextUtils.isEmpty(avatar)) {
                Glide.with(context).load(avatar).into(holder.images);
            }

            holder.imageBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    commentSnsCancel(info, null);
                    // commentSns(info, "comment 测试", null);

                }
            });
        }
        return convertView;
    }

    private void commentSns(SnsInfo snsInfo, String content, SnsComment replyComment) {
        SnsCommentRequest request = new SnsCommentRequest();
        request.setSnsId(snsInfo.getSnsId());
        request.setReplayComment(replyComment);
        request.setContent(content);
        Messenger.Companion.sendCommand(TimelineActivity.genCommand("snsComment", request), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("Sns comment result:" + result);
            }
        });
    }

    private void commentSnsCancel(SnsInfo snsInfo, SnsComment replyComment) {
        Messenger.Companion.sendCommand(TimelineActivity.genCommand("snsCommentCancel", snsInfo.getSnsId(), 33), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("Sns comment result:" + result);
            }
        });
    }

    private void likeComment(SnsInfo info) {
        Messenger.Companion.sendCommand(TimelineActivity.genCommand("cancelLikeSns", info.getSnsId()), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                CommandResult result1 = StrUtils.fromJson(result, CommandResult.class);
                if (!result1.isSuccess()) {
                    Utils.showToast("点赞失败");
                }
            }
        });
    }

    private String getLikeText(SnsInfo info) {
        if (info == null || info.getLikes() == null) {
            return "";
        }

        return StrUtils.join(info.getLikes(), ", ", new StrUtils.StringGetter<SnsLike>() {
            @Override
            public String getString(SnsLike data) {
                return data.getNickName();
            }
        });
    }

    private void showLogoSwitchWindow() {
        new ActionSheetDialog(context)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                takePicture();
                            }
                        })
                .addSheetItem("从相册中选择", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                openAlbum();
                            }
                        })
                .show();
    }

    private String getFriendNickNameById(String wechatId) {
        if (contacts.isEmpty()) {
            return wechatId;
        }

        for (Friend f : contacts) {
            if (StrUtils.stringNotNull(wechatId).equals(f.getWechatId())) {
                return f.getNickname();
            }
        }

        return wechatId;
    }


    private String getFriendAvatarById(String wechatId) {
        if (contacts.isEmpty()) {
            return wechatId;
        }

        for (Friend f : contacts) {
            if (StrUtils.stringNotNull(wechatId).equals(f.getWechatId())) {
                return f.getAvatar();
            }
        }

        return wechatId;
    }

    private void openAlbum() {
      /*  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");*/

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
     /*
       Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);*/
        ((TimelineActivity) context).startActivityForResult(intent, PHOTOZOOM);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), TEMP_JPG_NAME)));
        ((TimelineActivity) context).startActivityForResult(intent, PHOTOHRAPH);
    }


    private static class ViewHolder {
        private TextView name;
        private ImageView images;
        private ImageView contentimage;
        private TextView content;
        private TextView date;
        private TextView praise;
        private TextView commentary;
        private GridView gridView;
        private View imageBtn;
    }

}
