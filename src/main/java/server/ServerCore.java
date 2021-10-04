package server;

import server.sql.Student;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static server.ServerSqlHandler.*;

public class ServerCore {
    private static boolean flagIsClassTime = false;
    private static String classId = "class1"; //默认为 class1
    private static Map <Integer, Socket> studentSockets;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);

        System.out.println("教师端已启动");
        System.out.println("教师端：" + server.getInetAddress() + ":" + server.getLocalPort());

        //教师端开始接受操作
        System.out.println("当前班级：" + getClassId());
        ServerOptionHandler serverOptionHandler = new ServerOptionHandler();
        serverOptionHandler.start();

        //教师端进行对多学生端信息的接收处理
        studentSockets = new HashMap<>();
        for(;;) {
            Socket client = server.accept();
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

    public static Map<Integer, Socket> getStudentSockets() {
        return studentSockets;
    }
    public static Socket getStudentSockets(Integer studentId) {
        return studentSockets.get(studentId);
    }
    public static void setStudentSockets(Integer studentId, Socket socket) {
        if (socket == null) {
            studentSockets.remove(studentId);
        }
        else {
            studentSockets.put(studentId, socket);
        }
    }
}