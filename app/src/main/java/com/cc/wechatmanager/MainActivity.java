package com.cc.wechatmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.cc.core.wechat.model.sns.SnsInfo;
import com.cc.wechatmanager.friendscircle.activity.TimelineActivity;
import com.cc.wechatmanager.model.CommandResult;
import com.cc.wechatmanager.model.ContactsResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    RecyclerView contactListView, messageListView;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageAdapter = new MessageAdapter();
        contactListView = findViewById(R.id.contactsList);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView = findViewById(R.id.message_list);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setAdapter(messageAdapter);

        findViewById(R.id.getWechatInfoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Messenger.Companion.sendCommand(genCommand("getSnsList", 0), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "getSnsList Result:" + result);
                        Utils.showToast(result);
                    }
                });*/
                Intent i = new Intent(MainActivity.this, TimelineActivity.class);
                startActivity(i);
                //likeSns(-5427454008293773151L);
                /*Messenger.Companion.sendCommand(genCommand("uploadSns", genCardSnsInfo()), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "uploadImageSns Result:" + result);
                        Utils.showToast(result);
                    }
                });*/
               // updateRemark();
                //addGroupMember();
                /*Messenger.Companion.sendCommand(genCommand("createGroup", "xnhjcc", "wxid_smj74r8sn48o22", "wxid_ma5kf46xhg5d22", "denghongxing997955"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "createGroup Result:" + result);
                    }
                });*/

                /*Messenger.Companion.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
                    @Override
                    public void onResult(final String result) {

                        KLog.e("---->>.", "getLoginUserInfo Result:" + result);
                        contactListView.post(new Runnable() {
                            @Override
                            public void run() {
                                LoginUserResult result1 = StrUtils.fromJson(result, LoginUserResult.class);
                                if (result1.isSuccess()) {
                                    View v = findViewById(R.id.wechatInfo);
                                    TextView tv = v.findViewById(R.id.name);
                                    tv.setText(result1.getData().getNickname());
                                    tv = v.findViewById(R.id.wechatId);
                                    tv.setText(result1.getData().getWechatId());
                                    ImageView iv = v.findViewById(R.id.avatar);
                                    Glide.with(TimelineActivity.this).load(new File(result1.getData().getAvatar())).into(iv);
                                }
                            }
                        });


                    }
                });*/
                /*getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });
                        Messenger.Companion.sendCommand(genCommand("wechat:addFriend"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "addFriend Result:" + result);
                                Messenger.Companion.sendCommand(genCommand("wechat:getContacts"), new Callback() {
                                    @Override
                                    public void onResult(String result) {

                                        KLog.e("---->>.", "Result:" + result);

                                    }
                                });
                            }
                        });
                    }
                }, 1000);*/
            }
        });

        findViewById(R.id.getContactsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messenger.Companion.sendCommand(genCommand("getContacts"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "addFriend Result:" + result);
                        ContactsResult contacts = StrUtils.fromJson(result, ContactsResult.class);
                        final ContactsAdapter adapter = new ContactsAdapter(contacts.getData());

                        contactListView.post(new Runnable() {
                            @Override
                            public void run() {
                                contactListView.setAdapter(adapter);
                            }
                        });
                    }
                });
            }
        });

        findViewById(R.id.addFriendPhoneBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.addFriendPhoneInput);
                if (TextUtils.isEmpty(et.getText())) {
                    Toast.makeText(MainActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
                String phone = et.getText().toString();
                et = findViewById(R.id.addFriendSayHiInput);
                String sayHi = et.getText().toString();

                Messenger.Companion.sendCommand(genCommand("addFriend", phone, sayHi), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "addFriend Result:" + result);
                    }
                });
            }
        });
        findViewById(R.id.sendTextMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv = findViewById(R.id.sendMsgToInput);
                String to = tv.getText().toString();
                if (TextUtils.isEmpty(to)) {
                    Toast.makeText(MainActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                    return;
                }


                tv = findViewById(R.id.sendMsgContentInput);
                String content = tv.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(MainActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                CardMessage msg = new CardMessage();
                msg.setCreateTime(System.currentTimeMillis() / 1000);
                msg.setTarget(to);
                msg.setDescription(content);
                msg.setTitle("我是卡片消息");
                msg.setThumbUrl("http://g.hiphotos.baidu.com/image/h%3D300/sign=9b698df937f33a87816d061af65d1018/8d5494eef01f3a2963a5db079425bc315d607c8d.jpg");
                msg.setUrl("http://www.baidu.com");

                Messenger.Companion.sendCommand(genCommand("sendMessage", msg), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "send card Message Result:" + result);
                    }
                });

                TextMessage message = new TextMessage();
                message.setCreateTime(System.currentTimeMillis() / 1000);
                message.setTarget(to);
                message.setContent(content);

                Messenger.Companion.sendCommand(genCommand("sendMessage", message), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "sendMessage Result:" + result);
                    }
                });
            }

        });
        findViewById(R.id.sendImageMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });*/
                        EditText tv = findViewById(R.id.sendMsgToInput);
                        String to = tv.getText().toString();
                        if (TextUtils.isEmpty(to)) {
                            Toast.makeText(MainActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        tv = findViewById(R.id.sendMsgContentInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(MainActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ImageMessage msg = new ImageMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(to);
                        msg.setImageUrl(content);
                        msg.setFrom("denghongxing997955");

                        Messenger.Companion.sendCommand(genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
        findViewById(R.id.sendVideoMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });*/
                        EditText tv = findViewById(R.id.sendMsgToInput);
                        String to = tv.getText().toString();
                        if (TextUtils.isEmpty(to)) {
                            Toast.makeText(MainActivity.this, "请输入接收者", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        tv = findViewById(R.id.sendMsgContentInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(MainActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        VideoMessage msg = new VideoMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(to);
                        msg.setVideoUrl(content);

                        Messenger.Companion.sendCommand(genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
        registerMessageBroadcast();
        Messenger.Companion.sendCommand(genCommand("initDelayHooks"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {

            }
        });
    }

    private void likeSns(long snsId) {
        Messenger.Companion.sendCommand(genCommand("snsLike", snsId + ""), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("---->>.", "Comment Result:" + result);
            }
        });
    }

    static boolean bool = false;
    private void addGroupMember() {
        ArrayList<String> members = new ArrayList<>();
        members.add("wxid_w4y2nc022m0g22");
        Messenger.Companion.sendCommand(genCommand(bool ? "addGroupMember" : "removeGroupMember", "11456227155@chatroom", members), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("---->>.", "addGroupMember Result:" + result);
                CommandResult result1 = StrUtils.fromJson(result, CommandResult.class);
                if (result1 == null) {
                    return;
                }

                if (!result1.isSuccess()) {
                    Utils.showToast(result1.getMessage());
                }
            }
        });
        bool = !bool;
    }

    private SnsInfo genTextSnsInfo() {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription("Hello Wechat!!");
        snsInfo.setSnsType(SnsInfo.TEXT_TYPE);
        return snsInfo;
    }

    private SnsInfo genVideoSnsInfo() {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription("");
        snsInfo.setSnsType(SnsInfo.VIDEO_TYPE);
        ArrayList<String> medias = new ArrayList<>();
        medias.add("/mnt/sdcard/23.mp4");
        snsInfo.setMedias(medias);
        return snsInfo;
    }

    private SnsInfo genImageSnsInfo() {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription("Hello Wechat!!");
        snsInfo.setSnsType(SnsInfo.IMAGE_TYPE);
        snsInfo.setMedias(genMedias());
        return snsInfo;
    }

    private SnsInfo genCardSnsInfo() {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setShareTitle("检察机关认定河北涞源反杀案为正当防卫 决定不起诉女生父母");
        snsInfo.setDescription("检察机关认定河北涞源反杀案为正当防卫 决定不起诉女生父母");
        snsInfo.setSnsType(SnsInfo.CARD_TYPE);
        snsInfo.setUrl("https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9762242423668539096%22%7D&n_type=0&p_from=1");
        ArrayList<String> medias = new ArrayList<>();
        medias.add("http://b.hiphotos.baidu.com/image/pic/item/11385343fbf2b2114a65cd70c48065380cd78e41.jpg");
        snsInfo.setMedias(medias);
        return snsInfo;
    }

    private ArrayList<String> genMedias()  {
        ArrayList<String> medias = new ArrayList<>();
        medias.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1551376220&di=6a41bd2c22bb7f0e3fd772c4f059b1c9&src=http://wynews.zjol.com.cn/pic/0/13/62/36/13623698_728500.jpg");
        medias.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1551376220&di=3c6c0c16498be280fabfc0ea845766dd&src=http://img1.cache.netease.com/catchpic/0/06/06F2EA2ED8ABCF9ACC81075B0EBC59E8.jpg");
        medias.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1551376220&di=bb82e98a6fe065e2b948aa796f4ee552&src=http://img3.yxlady.com/yl/UploadFiles_5361/20121018/20121018130555357.jpg");

        medias.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1551386251722&di=1887f2eb90274f81d54043c5c5646c47&imgtype=0&src=http%3A%2F%2Fpic.962.net%2Fup%2F2017-7%2F20177201510397806.jpg");
        medias.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1551386251721&di=bbd3e14fe1aa902143601aacf055ab69&imgtype=0&src=http%3A%2F%2Fimg.11665.com%2Fimg01_p%2Fi1%2F16873022720357622%2FT1BESzXqdhXXXXXXXX_%2521%25210-item_pic.jpg");
        return medias;
    }

    private void updateRemark() {
        Messenger.Companion.sendCommand(genCommand("updateFriendRemark", "wxid_ma5kf46xhg5d22", "ce_shi"), new Callback() {
            @Override
            public void onResult(String result) {

                KLog.e("---->>.", "sendMessage Result:" + result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Command genCommand(String key, Object... data) {
        Command c = new Command();
        c.setKey(key);
        c.setId(UUID.randomUUID().toString());
        List<Object> args = new ArrayList<>(Arrays.asList(data));
        c.setArgs(args);
        return c;
    }

    private void registerMessageBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageUtils.RECEIVE_MESSAGE_BROADCAST);

        registerReceiver(receiver, filter);
    }

    private MessageReceiver receiver = new MessageReceiver();

    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String details = intent.getStringExtra("msg");
            WeChatMessage msg = MessageUtils.Companion.messageDeserializeGson().fromJson(details, WeChatMessage.class);
            messageAdapter.addMessage(msg);
        }
    }
}
