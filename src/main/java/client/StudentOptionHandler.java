package client;

import com.alibaba.fastjson.JSON;
import message.Message;

import java.io.*;
import java.net.Socket;

import static client.ClientCore.setFlagIsLogIn;


public class StudentOptionHandler extends Thread {

    private Socket client;

    private void printOptionList() {
        System.out.println("[Opt0]登出  [Opt1]举手  [Opt2]修改密码  [Opt3]在聊天室发言");
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
        BufferedReader input = new BufferedReader(new InputStreamReader(in));//键盘输入

        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);//客户端输出

        for (; ; ) {
            printOptionList();
            String str = input.readLine().toLowerCase();
            switch (str) {
                case "opt0"://登出
                    setFlagIsLogIn(false);
                    break;
                case "opt1"://举手
                    System.out.print("请输入举手消息内容：");
                    str = input.readLine();
                    socketPrintStream.println(JSON.toJSONString(new Message("Query", str)));
                    System.out.println("举手消息已发出");
                    break;
                case "opt2"://修改密码
                    System.out.print("请输入新的密码：");
                    str = input.readLine().toLowerCase();
                    System.out.println("是否确认将密码修改为 " + str + "?  [Opt1]确认  [Opt2]取消" );
                    String str2 = input.readLine();

                    socketPrintStream.println(JSON.toJSONString(new Message("Password", str)));
                    System.out.println("密码已修改");
                    break;
                case "opt3"://在聊天室发言
                    System.out.print("请输入消息内容：");
                    str = input.readLine();
                    socketPrintStream.println(JSON.toJSONString(new Message("Chat", str)));
                    System.out.println("消息已发出");
                    break;
                default:
                    System.out.println("操作无效，请重新输入");
                    break;
            }
        }
    }
}