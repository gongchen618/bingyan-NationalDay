package client;

import com.alibaba.fastjson.JSON;
import server.sql.Student;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;

import static client.ClientService.getDate;
import static client.LogInSystem.LogIn;

public class ClientChatter {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 8888), 3000);
        System.out.println("已发起连接");
        System.out.println("学生端：" + socket.getLocalAddress() + ":" + socket.getLocalPort());
        System.out.println("教师端：" + socket.getInetAddress() + ":" + socket.getPort());

        try{
            LogIn(socket);
        } catch (Exception e){
            System.out.println("异常关闭");
            e.printStackTrace();
        }

        socket.close ();
        System.out.println("学生端已退出");
    }


}