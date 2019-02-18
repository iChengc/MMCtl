package com.cc.wechatmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;
import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sendImageMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
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
                }, 1000);
            }
        });

        findViewById(R.id.addFriendPhoneBtn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
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
       /* getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                                Messenger.sendCommand(genCommand("openWechats"), new Callback() {
                                    @Override
                                    public void onResult(String result) {

                                        KLog.e("---->>.", "Result:" + result);
                                        Messenger.sendCommand(genCommand("addFriends"), new Callback() {
                                            @Override
                                            public void onResult(String result) {

                                                KLog.e("---->>.", "Result:" + result);

                                            }
                                        });
                                    }
                                });
            }
        }, 1000);*/
        /*getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Messenger.sendCommand(genCommand("openWechats"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        isStartAccessibilityService(MainActivity.this, "gg");
                        KLog.e("---->>.", "Result:" + result);

                    }
                });


            }
        }, 1000);*/
        //Rpc.asRpcServer();
        /*Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Command genCommand(String key) {
        Command c = new Command();
        c.setKey(key);
        List<Object> args = new ArrayList<>();
        args.add("wxfake");
        args.add("wxfake1");
        args.add("winnielala0323");
        c.setArgs(args);
        return c;
    }

    private Command genCommand(String key, Object... data) {
        Command c = new Command();
        c.setKey(key);
        List<Object> args = new ArrayList<>(Arrays.asList(data));
        c.setArgs(args);
        return c;
    }
}
