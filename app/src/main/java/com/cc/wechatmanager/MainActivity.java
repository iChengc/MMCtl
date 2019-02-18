package com.cc.wechatmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;

import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.model.ImageMessage;
import com.cc.core.wechat.model.TextMessage;
import com.cc.core.wechat.model.VideoMessage;
import com.cc.wechatmanager.model.ContactsResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView contactListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactListView = findViewById(R.id.contactsList);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.getWechatInfoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messenger.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "getLoginUserInfo Result:" + result);

                    }
                });
                /*getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Messenger.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });
                        Messenger.sendCommand(genCommand("wechat:addFriend"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "addFriend Result:" + result);
                                Messenger.sendCommand(genCommand("wechat:getContacts"), new Callback() {
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
                Messenger.sendCommand(genCommand("getContacts"), new Callback() {
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

                Messenger.sendCommand(genCommand("addFriend", phone, sayHi), new Callback() {
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

                TextMessage msg = new TextMessage();
                msg.setCreateTime(System.currentTimeMillis());
                msg.setTarget(to);
                msg.setContent(content);

                Messenger.sendCommand(genCommand("sendMessage", msg), new Callback() {
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
                        /*Messenger.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
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

                        Messenger.sendCommand(genCommand("sendMessage", msg), new Callback() {
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
                        /*Messenger.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
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

                        Messenger.sendCommand(genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Command genCommand(String key, Object... data) {
        Command c = new Command();
        c.setKey(key);
        List<Object> args = new ArrayList<>(Arrays.asList(data));
        c.setArgs(args);
        return c;
    }
}
