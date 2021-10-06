package client;

import com.alibaba.fastjson.JSON;
import server.StudentMessageHandler;
import server.sql.Student;

import java.io.*;
import java.net.Socket;

import static client.ClientCore.isFlagIsLogIn;
import static client.ClientCore.setFlagIsLogIn;

public class TeacherMessageReceiver extends Thread {

    private Socket client;

    public TeacherMessageReceiver(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        super.run();
        try {
            LogIn();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("与教师端的连接断开");
        }
    }

    private void LogIn() throws IOException {
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        /*
            String str = input.readLine();//键盘读入
            socketPrintStream.println(str);//向教师端输出
            String echo = socketBufferedReader.readLine();//从教师端读入
        */

        do {
            Student student = new Student();
            System.out.print("请输入学号：");
            socketPrintStream.println(input.readLine());
            System.out.print("请输入密码：");
            socketPrintStream.println(input.readLine());

            String echo = socketBufferedReader.readLine();
            System.out.println(echo);
            if (echo.indexOf("登录成功") != -1) {
                setFlagIsLogIn(true);
            } else {
                System.out.println("请重新输入");
            }
        } while (!isFlagIsLogIn());

        String str = null;
        do { //心跳包，以及只有这里会接受教师端的信息
            str = socketBufferedReader.readLine();
            if (str != null) socketPrintStream.println("");
            if (str != null && str.length() > 0) {
                TeacherMessageHandler teacherMessageHandler = new TeacherMessageHandler(str, client);
                teacherMessageHandler.start();
            }
        } while (str != null);

        setFlagIsLogIn(false);

        input.close();
        socketBufferedReader.close();
        socketPrintStream.close();
    }
}