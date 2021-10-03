package client;

import com.alibaba.fastjson.JSON;
import server.sql.Student;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;

import static client.ClientService.getDate;

public class ClientChatter {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 8888), 3000);
        System.out.println("已发起连接");
        System.out.println("学生端：" + socket.getLocalAddress() + ":" + socket.getLocalPort());
        System.out.println("教师端：" + socket.getInetAddress() + ":" + socket.getPort());

        try{
            TalkToServer(socket);
        } catch (Exception e){
            System.out.println("异常关闭");
            e.printStackTrace();
        }

        socket.close ();
        System.out.println("学生端已退出");
    }

    private static void TalkToServer(Socket client) throws IOException {

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

        boolean flagConnect = false;

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
                flagConnect = true;
            } else {
                System.out.println("请重新输入");
            }

        } while (!flagConnect);

        input.close();
        socketBufferedReader.close();
        socketPrintStream.close();

        while (true) {
            System.out.print("[上一次刷新：" + getDate() + "]登录状态：正常\r");
            socketPrintStream.println("check online");
            try {
                Thread.sleep(2000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}