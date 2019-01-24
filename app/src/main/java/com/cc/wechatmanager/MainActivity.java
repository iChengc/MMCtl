package com.cc.wechatmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.rpc.Rpc;
import com.cc.core.rpc.RpcServer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messenger.sendCommand(genCommand("openUrl"), new Callback() {
                    @Override
                    public void onResult(String result) {

                    }
                });
            }
        });

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Messenger.sendCommand(genCommand("openWechat"), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "Result:" + result); getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Messenger.sendCommand(genCommand("openUrl"), new Callback() {
                                    @Override
                                    public void onResult(String result) {

                                        KLog.e("---->>.", "Result:" + result);
                                    }
                                });

                            }
                        }, 5000);
                    }
                });


            }
        }, 1000);
        //Rpc.asRpcServer();
    }

    private Command genCommand(String key) {
        Command c = new Command();
        c.setKey(key);
        List<String> args = new ArrayList<>();
        args.add("https://pan.baidu.com");
        c.setArgs(args);
        return c;
    }
}
