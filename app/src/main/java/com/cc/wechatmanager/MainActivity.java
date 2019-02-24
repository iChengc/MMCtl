package com.cc.wechatmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;

import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.LoginUserResult;

import org.jetbrains.annotations.Nullable;

import java.io.File;
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

                Messenger.Companion.sendCommand(genCommand("getGroupInfo", "4828574009@chatroom", "14870919873@chatroom"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "getGroupInfo Result:" + result);
                    }
                });
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
                                    Glide.with(MainActivity.this).load(new File(result1.getData().getAvatar())).into(iv);
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
