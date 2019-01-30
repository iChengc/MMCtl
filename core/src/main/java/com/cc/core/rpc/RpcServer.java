package com.cc.core.rpc;

import android.text.TextUtils;

import com.cc.core.Constant;
import com.cc.core.actions.ActionResult;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
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
    public void start(final int port) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                startInternal(port);
            }
        });
    }
    public void startInternal(int port) {
        do {
            try {
                if (server != null) {
                    server.close();
                }
                KLog.e("RpcServer", ">>>> Start rpc server");
                InetAddress address = InetAddress.getLoopbackAddress();
                server = new ServerSocket(port, 50, address);
                while (!disconnect) {
                    Socket client = server.accept();
                    executor.submit(responseClient(client));
                }
            } catch (IOException e) {
                e.printStackTrace();
                KLog.e("RpcServer", "Start server socket error! Try to reconnect", e);
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
                DataInputStream in  = null;
                OutputStream out = null;
                try { //接受消息

                    KLog.e("RpcServer", "a new client has extablish");
                     in = new DataInputStream(client.getInputStream());
                     String sb = in.readUTF();
                    //回复消息
                    //PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    /*String s;
                    StringBuffer sb = new StringBuffer();
                    while ((s = in.readLine()) != null) {
                        sb.append(s);
                    }*/
                    KLog.e(TAG, "收到客户端的消息：" + sb);
                    out = client.getOutputStream();
                    ActionResult result;
                    if (TextUtils.isEmpty(sb)) {
                        result = ActionResult.Companion.failedResult("Empty rpc request");
                    } else {
                        result = Rpc.invoke(sb);
                    }
                    out.write(StrUtils.toJson(result).getBytes(Charset.forName("utf-8")));
                    out.flush();
                } catch (Exception e) {
                    if (out != null) {
                        try {
                            out.write(StrUtils.toJson(ActionResult.Companion.failedResult("failed to call rpc:" + e.getMessage())).getBytes(Charset.forName("utf-8")));
                            out.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }

                        if (out != null) {
                            out.close();
                        }
                        if (client != null) {
                            client.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
