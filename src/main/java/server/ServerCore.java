package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static server.ServerSqlHandler.*;

public class ServerCore {
    private static boolean flagIsClassTime = false;
    static public String classId = "class1"; //默认为 class1

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);

        System.out.println("教师端已启动");
        System.out.println("教师端：" + server.getInetAddress() + ":" + server.getLocalPort());

        System.out.println("当前班级：" + getClassId());
        ServerOptionHandler serverOptionHandler = new ServerOptionHandler();
        serverOptionHandler.start();

        for(;;) {
            Socket client = server.accept();
            System.out.println(client);
            StudentMessageReceiver studentMessageReceiver = new StudentMessageReceiver(client);
            studentMessageReceiver.start();
        }
    }

    public static boolean isFlagIsClassTime() {
        return flagIsClassTime;
    }
    public static void setFlagIsClassTime(boolean flag) {
        flagIsClassTime = flag;
        System.out.println("当前上课状态已修改为：" + flagIsClassTime);
    }
}