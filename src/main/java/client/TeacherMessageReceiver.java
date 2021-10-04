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
            e.printStackTrace();
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
            if (echo.charAt (1) == 'T') {
                setFlagIsLogIn(true);
            } else {
                System.out.println("请重新输入");
            }
        } while (!isFlagIsLogIn());

        String str = null;
        do { //心跳包，以及只有这里会接受教师端的信息
            str = socketBufferedReader.readLine();
            socketPrintStream.println("");
            if (str != null && str.length() > 0) {
                TeacherMessageHandler teacherMessageHandler = new TeacherMessageHandler(str);
                teacherMessageHandler.start();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (str != null);

        input.close();
        socketBufferedReader.close();
        socketPrintStream.close();
    }
}