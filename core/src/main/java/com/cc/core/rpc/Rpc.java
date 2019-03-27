package com.cc.core.rpc;

import android.text.TextUtils;

import com.cc.core.Constant;
import com.cc.core.actions.ActionResult;
import com.cc.core.actions.Actions;
import com.cc.core.utils.StrUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Rpc {

    private static RpcServer server;

    private static int getPort(RpcArgs rpcArgs) {
        if (rpcArgs != null && rpcArgs.getType().equals(RpcArgs.CallType.EXECUTE_DB)) {
            return Constant.DBHANDLER_PORT;
        }

        return Constant.SOCKET_PORT;
    }

    public static ActionResult call(RpcArgs rpcArgs) {
        return call(rpcArgs.getId(), StrUtils.toNumberJson(rpcArgs), getPort(rpcArgs));
    }

    public static ActionResult call(String id, String message, int port) {

        DataOutputStream out = null;
        BufferedReader in = null;
        Socket client = null;
        try {

            SocketAddress sa = new InetSocketAddress(InetAddress.getLoopbackAddress().getHostAddress(), port);
            client = new Socket(/*address, Constant.SOCKET_PORT*/);
            client.setSoTimeout(300000);
            client.setTcpNoDelay(true);
            client.setSoLinger(true, 1);
            client.setReuseAddress(true);
            client.connect(sa);

            // 发送rpc调用参数
            out = new DataOutputStream(client.getOutputStream());
            //byte[] data = message.getBytes(Charset.forName("utf-8"));
            out.writeUTF(message);
            out.flush();

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String s;
            StringBuffer sb = new StringBuffer();
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }

            if (TextUtils.isEmpty(sb.toString())) {
                return ActionResult.Companion.failedResult(id, "Empty response");
            }
            return StrUtils.fromNumberJson(sb.toString(), ActionResult.class);
            /*out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            out.write(message, 0, message.length());
            out.flush();*/
            //eturn ActionResult.successResult();
            // 接受rpc返回值
            /*in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String s;
            StringBuffer sb = new StringBuffer();
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }

            if (TextUtils.isEmpty(sb.toString())) {
                return null;
            }
            return StrUtils.fromJson(sb.toString(), ActionResult.class);*/
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.Companion.failedResult(id, e);
        } finally {

            try {
                if (client != null) {
                    client.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static ActionResult invoke(RpcArgs msg) {
        switch (msg.getType()) {
            case RpcArgs.CallType.EXECUTE_ACTION:
                return Actions.Companion.receivedAction(msg.getData());
            default:
                return null;
        }
    }

    static ActionResult invoke(String message) {
        RpcArgs msg = RpcArgs.from(message);
        return invoke(msg);
    }

    public static void asRpcServer() {
        asRpcServer(Constant.SOCKET_PORT);
    }

    public static void asRpcServer(int port) {
        if (server != null) {
            server.stop();
        }
        server = new RpcServer();
        server.start(port);
    }
}
