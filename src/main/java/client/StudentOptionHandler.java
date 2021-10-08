package client;

import com.alibaba.fastjson.JSON;
import message.Message;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

import static client.ClientCore.*;


public class StudentOptionHandler extends Thread {

    private Socket client;

    private void printOptionList() {
        System.out.println("[Opt0]登出  " +
                "[Opt1]与老师私聊  " +
                "[Opt2]修改密码  " +
                "[Opt3]在聊天室发言  " +
                "[Opt4]向老师发送文件  " +
                "[Opt5]提交测试");
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

        String addr = null;
        File file = null;

        for (; ; ) {
            printOptionList();
            String str = input.readLine().toLowerCase();
            switch (str) {
                case "opt0"://登出
                    setFlagIsLogIn(false);
                    break;
                case "opt1"://举手
                    System.out.print("请输入私聊消息内容：");
                    str = input.readLine();
                    socketPrintStream.println(JSON.toJSONString(new Message("Query", str)));
                    System.out.println("私聊消息已发出");
                    break;
                case "opt2"://修改密码
                    System.out.print("请输入新的密码：");
                    str = input.readLine().toLowerCase();
                    System.out.println("是否确认将密码修改为 " + str + "?  [Opt1]确认  [Opt2]取消" );
                    String str2 = input.readLine();
                    if (str2.equalsIgnoreCase("opt1")) {
                        socketPrintStream.println(JSON.toJSONString(new Message("Password", str)));
                        System.out.println("密码已修改");
                    } else {
                        System.out.println("操作已取消");
                    }
                    break;
                case "opt3"://在聊天室发言
                    System.out.print("请输入消息内容：");
                    str = input.readLine();
                    socketPrintStream.println(JSON.toJSONString(new Message("Chat", str)));
                    System.out.println("消息已发出");
                    break;
                case "opt4"://向老师发送文件
                    addr = new File("").getCanonicalPath() + "/";
                    System.out.println("[提示]需要发送的文件应放置在：" + addr);
                    System.out.print("请输入文件名字(可包含子文件夹路径)：");

                    str = input.readLine();
                    file = new File(addr + str);
                    if (file.exists()) {
                        FileInputStream fileInputStream = new FileInputStream(str);

                        byte[] buff = new byte[2048];
                        socketPrintStream.println(JSON.toJSONString(new Message("Document", file.getName())));
                        socketPrintStream.println(JSON.toJSONString(new Message("Document", String.valueOf(file.length()))));

                        int length = 0;
                        while ((length = fileInputStream.read(buff)) != -1) {
                            byte[] trueBuff = new byte[length];
                            System.arraycopy(buff, 0, trueBuff, 0, length);
                            socketPrintStream.println(JSON.toJSONString(new Message("Document", Base64.getEncoder().encodeToString(trueBuff))));
                        }
                        System.out.println("文件已发送");
                    } else {
                        System.out.println("文件不存在！[提示]可发送的文件包括：");
                        String[] documents = (new File(addr)).list();
                        for (String document : documents) {
                            System.out.print(document + "  ");
                        }
                        System.out.println("");
                    }
                    break;
                case "opt5"://提交测试文件
                    Map<String, Boolean> map = getFileType();
                    if (map.isEmpty()) {
                        System.out.println("没有待提交的测试！");
                        break;
                    }

                    System.out.println("待提交的测试：");

                    if (map.containsValue(false)) {
                        System.out.print("客观题：");
                        for (Map.Entry<String, Boolean> entry : map.entrySet())
                            if (entry.getValue() == false) {
                                System.out.print(entry.getKey() + "  ");
                            }
                        System.out.println("");
                    }
                    if (map.containsValue(true)) {
                        System.out.print("主观题：");
                        for (Map.Entry<String, Boolean> entry : map.entrySet())
                            if (entry.getValue() == true) {
                                System.out.print(entry.getKey() + "  ");
                            }
                        System.out.println("");
                    }

                    System.out.print("请输入你要提交的测试名称：");
                    String test = input.readLine();
                    if (map.containsKey(test) == false) {
                        System.out.println("测试不存在！");
                        break;
                    }

                    String type = null;
                    if (map.get(test) == true) type = "Test";
                    else {
                        System.out.println ("[提示]客观题请将第 n 个小空的答案填在提交文件的第 n 行，不要有多余的空行和空格");
                        type = "Mark";
                    }

                    addr = new File("").getCanonicalPath() + "/";
                    System.out.println("[提示]需要提交的测试答案文件应放置在：" + addr);
                    System.out.print("请输入测试答案文件名(可包含子文件夹路径)：");

                    str = input.readLine();
                    file = new File(addr + str);
                    if (file.exists()) {
                        FileInputStream fileInputStream = new FileInputStream(str);

                        byte[] buff = new byte[2048];
                        socketPrintStream.println(JSON.toJSONString(new Message(type, test + "_" + file.getName())));
                        socketPrintStream.println(JSON.toJSONString(new Message(type, String.valueOf(file.length()))));

                        int length = 0;
                        while ((length = fileInputStream.read(buff)) != -1) {
                            byte[] trueBuff = new byte[length];
                            System.arraycopy(buff, 0, trueBuff, 0, length);
                            socketPrintStream.println(JSON.toJSONString(new Message(type, Base64.getEncoder().encodeToString(trueBuff))));
                        }
                        System.out.println("测试已提交");
                        setFileType(test, null);
                    } else {
                        System.out.println("文件不存在！[提示]可发送的文件包括：");
                        String[] documents = (new File(addr)).list();
                        for (String document : documents) {
                            System.out.print(document + "  ");
                        }
                        System.out.println("");
                    }
                    break;
                default:
                    System.out.println("操作无效，请重新输入");
                    break;
            }
        }
    }
}