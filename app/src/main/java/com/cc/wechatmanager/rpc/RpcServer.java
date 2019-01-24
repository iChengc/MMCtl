package com.cc.wechatmanager.rpc;

import android.text.TextUtils;

import com.cc.wechatmanager.Constant;
import com.cc.wechatmanager.log.KLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    private static final String TAG = "RpcServer";

    private boolean disconnect = false;
    private ExecutorService executor;
    private ServerSocket server;

    public RpcServer() {
        executor = Executors.newFixedThreadPool(5);
    }
    public void start() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                startInternal();
            }
        });
    }
    public void startInternal() {
        do {
            try {
                if (server != null) {
                    server.close();
                }
                server = new ServerSocket(Constant.SOCKET_PORT);
                while (!disconnect) {
                    Socket client = server.accept();
                    executor.submit(responseClient(client));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!disconnect);
    }

    public void stop() {
        disconnect = true;
        try {
            if (server != null) {
                server.close();
            }
            server = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //在这里接受和回复客户端消息
    private Runnable responseClient(final Socket client) {
        return new Runnable() {
            @Override
            public void run() {
                try { //接受消息

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //回复消息
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    String s = null;
                    StringBuffer sb = new StringBuffer();
                    while ((s = in.readLine()) != null) {
                        sb.append(s);
                    }
                    KLog.i(TAG, "收到客户端的消息：" + sb);
                    if (!TextUtils.isEmpty(sb.toString())) {
                        String result = Rpc.invoke(sb.toString());
                        out.print(result);
                    }

                    out.close();
                    in.close();
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
