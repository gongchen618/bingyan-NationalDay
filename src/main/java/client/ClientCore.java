package client;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientCore {

    private static boolean flagIsLogIn = false;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 8888), 3000);
        System.out.println("已发起连接 (port:" + socket.getLocalPort() + ")");
        //System.out.println("学生端：" + socket.getLocalAddress() + ":" + socket.getLocalPort());
        //System.out.println("教师端：" + socket.getInetAddress() + ":" + socket.getPort());

        TeacherMessageReceiver teacherMessageReceiver = new TeacherMessageReceiver(socket);
        teacherMessageReceiver.start();

        while (!isFlagIsLogIn()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } //保证其他操作在登录后才能进行

        StudentOptionHandler studentOptionHandler = new StudentOptionHandler(socket);
        studentOptionHandler.start();

        while (isFlagIsLogIn()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } //是不是其实不用多线程的？

        socket.close ();
        System.out.println("与教师端的连接已断开");
    }

    public static boolean isFlagIsLogIn() {
        return flagIsLogIn;
    }
    public static void setFlagIsLogIn(boolean flag) {
        flagIsLogIn = flag;
    }
}