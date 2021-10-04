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
        System.out.println(socket);
        System.out.println("已发起连接");
        System.out.println("学生端：" + socket.getLocalAddress() + ":" + socket.getLocalPort());
        System.out.println("教师端：" + socket.getInetAddress() + ":" + socket.getPort());

        LogInSystem logInSystem = new LogInSystem(socket);
        logInSystem.start();

        while (!isFlagIsLogIn()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        StudentOptionHandler studentOptionHandler = new StudentOptionHandler(socket);
        studentOptionHandler.start();

        while (isFlagIsLogIn()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        socket.close ();
        System.out.println("学生端已退出");
    }

    public static boolean isFlagIsLogIn() {
        return flagIsLogIn;
    }
    public static void setFlagIsLogIn(boolean flag) {
        flagIsLogIn = flag;
    }
}