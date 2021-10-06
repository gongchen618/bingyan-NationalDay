package server;

import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;
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
    private static int classTime;
    private static String classId; //默认为 class1
    private static Map <Integer, Socket> studentSockets;
    private static Map <Integer, Pair<FileOutputStream, Integer>> studentFileStreams;
    private static Map <Integer, Pair<FileOutputStream, Integer>> studentMonitorStreams;
    private static String studentFileDirectory;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);

        System.out.println("教师端已启动");
        //System.out.println("教师端：" + server.getInetAddress() + ":" + server.getLocalPort());

        //教师端开始接受操作
        setClassId("class1"); //这时有多个重置操作发生了
        setStudentFileDirectory(new File("").getCanonicalPath());

        ServerOptionHandler serverOptionHandler = new ServerOptionHandler();
        serverOptionHandler.start();

        //教师端进行对多学生端信息的接收处理
        studentSockets = new HashMap<>();
        studentFileStreams = new HashMap<>();
        studentMonitorStreams = new HashMap<>();

        for(;;) {
            Socket client = server.accept();
            StudentMessageReceiver studentMessageReceiver = new StudentMessageReceiver(client);
            studentMessageReceiver.start();
        }
    }


    public static String getClassId() {
        return classId;
    }
    public static void setClassId(String Id) {
        classId = Id;
        System.out.println("当前班级：" + classId);
        resetPerfomance(); //状态重置为 "缺席"
        setClassTime(0); //标记课堂尚未开始
        resetScreenStaticTime(); //屏幕静止时间重置为 0
    }

    public static int getClassTime() {
        return classTime;
    }
    public static void setClassTime(int classTime) {
        ServerCore.classTime = classTime;
        if (classTime == 0) System.out.println("当前未开始上课");
        else if (classTime == 1) System.out.println("当前是上课时间");
        else if (classTime == 2) System.out.println("当前课程已结束");
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
        } else {
            studentSockets.put(studentId, socket);
        }
    }
    public static void clearStudentSockets(){
        studentSockets.clear();
    }

    public static Map<Integer, Pair<FileOutputStream, Integer>> getStudentFileStreams() {
        return studentFileStreams;
    }
    public static Pair<FileOutputStream, Integer> getStudentFileStreams(Integer studentId) {
        if (studentFileStreams.containsKey(studentId)) {
            return studentFileStreams.get(studentId);
        } else {
            return null;
        }
    }
    public static void setStudentFileStreams(Integer studentId, FileOutputStream fileOutputStream, Integer length) {
        if (fileOutputStream == null) {
            studentFileStreams.remove (studentId);
        } else {
            studentFileStreams.put (studentId, Pairs.from(fileOutputStream, length));
        }
    }

    public static Map<Integer, Pair<FileOutputStream, Integer>> getStudentMonitorStreams() {
        return studentMonitorStreams;
    }
    public static Pair<FileOutputStream, Integer> getStudentMonitorStreams(Integer studentId) {
        if (studentMonitorStreams.containsKey(studentId)) {
            return studentMonitorStreams.get(studentId);
        } else {
            return null;
        }
    }
    public static void setStudentMonitorStreams(Integer studentId, FileOutputStream fileOutputStream, Integer length) {
        if (fileOutputStream == null) {
            studentMonitorStreams.remove (studentId);
        } else {
            studentMonitorStreams.put (studentId, Pairs.from(fileOutputStream, length));
        }
    }

    public static String getStudentFileDirectory() {
        return studentFileDirectory;
    }
    public static void setStudentFileDirectory(String studentFileDirectory) {
        ServerCore.studentFileDirectory = studentFileDirectory;
        File file = new File(studentFileDirectory);
        if (!file.exists()) file.mkdirs();
        System.out.println("当前接收文件夹路径：" + getStudentFileDirectory());
    }
}