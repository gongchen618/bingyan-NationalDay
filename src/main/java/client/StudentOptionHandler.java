package client;

import java.io.*;
import java.net.Socket;

import static client.ClientCore.setFlagIsLogIn;


public class StudentOptionHandler extends Thread {

    private Socket client;

    private static String getOpt() {
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));//从键盘读入
        String str = null;
        try {
            str = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void printOptionList() {
        System.out.println("[Opt0]登出  [Opt1]举手  [Opt2]修改密码");
    }

    public StudentOptionHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        super.run();
        try {
            option();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void option() throws IOException {
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        for (; ; ) {
            printOptionList();
            String str = getOpt();
            switch (str) {
                case "opt0":
                    setFlagIsLogIn(false);
                    break;
                case "opt1":
                    str = input.readLine();//键盘读入
                    socketPrintStream.println(str);//向教师端输出
                    break;
                case "opt2":
                    System.out.println("请输入新的密码：");
                    str = input.readLine();//键盘读入
                    System.out.println("是否确认将密码修改为 " + str + "?  [Opt1]确认  [Opt2]取消" );
                    socketPrintStream.println(str);//向教师端输出

                    str = socketBufferedReader.readLine();//从教师端输入
                    if (str.equalsIgnoreCase("Success")) {
                        System.out.println("修改密码成功");
                    } else {
                        System.out.println("修改密码失败");
                    }

                    break;
                default:
                    System.out.println(str + " 操作无效，请重新输入");
                    break;
            }
        }
    }
}