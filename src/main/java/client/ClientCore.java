package client;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;

public class ClientCore {

    private static boolean flagIsLogIn = false;
    private static int screenStaticTime = 0;
    private static Pair <FileOutputStream, Integer> fileFromTeacher = null;
    private static Map<String, Boolean> fileType = new HashMap<>(); //0客观 1主观

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

        ScreenStaticTimeAutoMachine screenStaticTimeAutoMachine = new ScreenStaticTimeAutoMachine(socket);
        screenStaticTimeAutoMachine.start();

        while (isFlagIsLogIn()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } //是不是其实不用多线程的？

        socket.close ();
        System.out.println("已下线");
    }

    public static boolean isFlagIsLogIn() {
        return flagIsLogIn;
    }
    public static void setFlagIsLogIn(boolean flag) {
        flagIsLogIn = flag;
    }

    public static int getScreenStaticTime() {
        return screenStaticTime;
    }
    public static void setScreenStaticTime(int screenStaticTime) {
        ClientCore.screenStaticTime = screenStaticTime;
    }

    public static Pair<FileOutputStream, Integer> getFileFromTeacher() {
        return fileFromTeacher;
    }
    public static void setFileFromTeacher(FileOutputStream fileOutputStream, Integer integer) {
        if (fileOutputStream == null) fileFromTeacher = null;
        else fileFromTeacher = Pairs.from(fileOutputStream, integer);
    }

    public static Map<String, Boolean> getFileType () {
        return fileType;
    }
    public static boolean getFileType (String file) {
        return fileType.get(file);
    }
    public static void setFileType (String file, Boolean type){
        if (type == null) fileType.remove(file);
        else fileType.put(file, type);
    }
}