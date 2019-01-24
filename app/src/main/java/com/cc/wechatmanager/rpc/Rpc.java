package com.cc.wechatmanager.rpc;

import android.text.TextUtils;

import com.cc.wechatmanager.Constant;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.actions.Actions;
import com.cc.wechatmanager.utils.StrUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Rpc {

    private static RpcServer server;
    public static ActionResult call(String message) throws IOException {

        Socket client;

        client = new Socket("localhost", Constant.SOCKET_PORT);

        // 发送rpc调用参数
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.print(message);

        // 接受rpc返回值
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String s;
        StringBuffer sb = new StringBuffer();
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }

        out.close();
        in.close();
        client.close();
        if (TextUtils.isEmpty(sb.toString())) {
            return null;
        }
        return StrUtils.fromJson(sb.toString(), ActionResult.class);
    }

    public static String invoke(String message) {
        Message msg = Message.from(message);
        switch (msg.getType()) {
            case Message.MessageType.COMMAND:
                return Actions.receivedAction(msg.getData());
            default:
                return "";
        }
    }

    public static void asRpcServer() {
        if (server != null) {
            server.stop();
        }
        server = new RpcServer();
        server.start();
    }
}
