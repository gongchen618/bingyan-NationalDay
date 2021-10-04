package client;

import com.alibaba.fastjson.JSON;
import server.sql.Student;

import java.io.*;
import java.net.Socket;

import static client.ClientCore.isFlagIsLogIn;
import static client.ClientCore.setFlagIsLogIn;

public class LogInSystem extends Thread {

    private Socket client;

    public LogInSystem(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        super.run();
        try {
            LogIn();
        } catch (IOException e) {
            System.out.println("加油");
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
            student.setStudentId(Integer.parseInt(input.readLine()));
            System.out.print("请输入密码：");
            student.setPassword(input.readLine());

            socketPrintStream.println(JSON.toJSONString(student));
            String echo = socketBufferedReader.readLine();

            System.out.println(echo);
            if ("Login Successfully".equalsIgnoreCase(echo)) {
                setFlagIsLogIn(true);
                System.out.println(socketBufferedReader.readLine());
            } else {
                System.out.println("请重新输入");
            }
        } while (!isFlagIsLogIn());

        while (true) {
            //System.out.println("[上一次刷新：" + getDate() + "]登录状态：正常");
            socketPrintStream.println("");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
                input.close();
                socketBufferedReader.close();
                socketPrintStream.close();
            }
        }
    }
}