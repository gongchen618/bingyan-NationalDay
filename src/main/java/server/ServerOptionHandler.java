package server;

import com.alibaba.fastjson.JSON;
import message.Message;
import server.sql.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import static server.ServerCore.*;
import static server.ServerSqlHandler.*;

public class ServerOptionHandler extends Thread {

    private void printOptionList (){
        System.out.println("[Opt0]切换班级  " +
                "[Opt1]重置/修改学生状态  " +
                "[Opt2]标记上课/下课  " +
                "[Opt4]发送消息  \n" +
                "[Opt5]修改接收文件夹路径  " +
                "[Opt6]查看学生屏幕  " +
                "[Opt7]批量导入学生名单  " +
                "[Opt8]删除班级  " +
                "[Opt8]发放作业");
    }

    public ServerOptionHandler() {
    }

    @Override
    public void run() {
        super.run();
        try{
            option();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void option() throws IOException{

        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));//从键盘读入
        Map <Integer, Socket> map = null;

        for (;;) {
            printOptionList();
            String str = input.readLine().toLowerCase();
            switch (str) {
                case "opt0": //切换班级
                    map = getStudentSockets();
                    if (map.isEmpty() == false) {
                        System.out.println("[警告]当前班级有学生处于登录状态，切换班级会使它们断开连接");
                        System.out.println("确认继续吗？[y/N]");
                        str = input.readLine().toLowerCase();
                        if (str.equals("y") == false) {
                            System.out.println("操作已取消");
                            break;
                        }
                    }
                    printTableName();
                    System.out.print("请输入新的班级：");
                    str = input.readLine();
                    if (!isTableExist(str)) {
                        System.out.println("该班级不存在！");
                        break;
                    } else if (str.equals(getClassId())) {
                        System.out.println("不能重复切换为当前班级！");
                    } else {
                        for (Map.Entry<Integer, Socket> entry : map.entrySet()){
                            PrintStream socketOutput = new PrintStream(entry.getValue().getOutputStream());//学生输出
                            socketOutput.println(JSON.toJSONString(new Message("Bye", str)));
                        }
                        clearStudentSockets();
                        setClassId(str);
                    }
                    break;
                case "opt1": //修改学生状态
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]强制重置所有学生状态为 \"缺席\"  [Opt1]修改单个学生状态  [Opt2]取消操作");

                        str = input.readLine().toLowerCase();
                        flag = true;

                        if (str.equals("opt0")) resetPerfomance();
                        else if (str.equals("opt1")) {
                            System.out.print("请输入目标学生学号：");
                            int studentId = Integer.parseInt(input.readLine());
                            Student student = getStudentById(studentId);
                            if (student != null) {
                                System.out.println(student.getIdAndName() + "当前状态为 " + student.getPerformance());
                                System.out.println("[Opt0]正常  " +
                                        "[Opt1]缺席  " +
                                        "[Opt2]迟到  " +
                                        "[Opt3]早退");
                                System.out.print("请输入新的状态编号：");
                                str = input.readLine().toLowerCase();
                                if (str.equals("opt0")) str = "正常";
                                else if (str.equals("opt1")) str = "缺席";
                                else if (str.equals("opt2")) str = "迟到";
                                else if (str.equals("opt3")) str = "早退";
                                else {
                                    System.out.println(" 操作无效，请重新输入");
                                    break;
                                }
                                changeStudentPerformance(studentId, str);
                            } else {
                                System.out.println("该学生不存在！");
                            }
                        } else if (str.equals("opt2")) {
                            System.out.println("操作已取消");
                        } else {
                            System.out.println(" 操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt2": //标记上课下课
                    for (boolean flag = false; !flag; ) {
                        setClassTime(getClassTime());
                        System.out.println("[Opt0]开始上课(开始登记迟到)  [Opt1]下课(学生状态定格)  [Opt2]取消操作");

                        str = input.readLine();
                        flag = true;

                        if (str.equals("opt0")) {
                            setClassTime(1);
                        }
                        else if (str.equals("opt1")) {
                            setClassTime(2);
                        }
                        else if (str.equals("opt2")) {
                            System.out.println("操作已取消");
                        }
                        else {
                            System.out.println(str + " 操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt3": //打印课堂状态
                    printStudentListByOrder();
                    break;
                case "opt4": //向学生发送消息
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]向单个学生发送  [Opt1]向全体学生广播  [Opt2]聊天室  [Opt3]取消操作");

                        str = input.readLine().toLowerCase();
                        flag = true;

                        if (str.equals("opt0")) {
                            System.out.print("请输入目标学生学号：");
                            int studentId = Integer.parseInt(input.readLine());
                            Socket socket = getStudentSockets(studentId);
                            if (socket == null) {
                                System.out.println("该学生不存在！");
                            } else {
                                System.out.print("请输入消息内容：");
                                str = input.readLine();

                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());//学生输出
                                socketOutput.println(JSON.toJSONString(new Message("Answer", str)));
                                System.out.println("消息已发送");
                            }
                        } else if (str.equals("opt1")) {
                            System.out.print("请输入广播消息内容：");
                            str = input.readLine();

                            map = getStudentSockets();
                            for (Map.Entry<Integer, Socket> entry : map.entrySet()) {
                                Socket socket = entry.getValue();
                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                socketOutput.println(JSON.toJSONString(new Message("BroadCast", str)));
                            }

                            System.out.println("消息已发送");
                        } else if (str.equals("opt2")) {
                            System.out.print("请输入聊天室消息内容：");
                            str = "【聊天室】@ 老师：" + input.readLine();

                            map = getStudentSockets();
                            for (Map.Entry<Integer, Socket> entry : map.entrySet()) {
                                Socket socket = entry.getValue();
                                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                                socketOutput.println(JSON.toJSONString(new Message("Chat", str)));
                            }

                            //System.out.println("消息已发送");
                            System.out.println(str);
                        } else if (str.equals("opt3")) {
                            System.out.println("操作已取消");
                        } else {
                            System.out.println("操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt5":
                    System.out.println("当前接收文件夹路径：" + getStudentFileDirectory());
                    System.out.print("确认要修改路径吗(已接收文件不会移动)[y/N]：");
                    str = input.readLine().toLowerCase();
                    if (str.equals("y")) {
                        System.out.print("请输入新的接收文件夹完整路径：");
                        str = input.readLine();
                        File directory = new File(str);
                        if (!directory.exists()) directory.mkdirs();
                        setStudentFileDirectory(str);
                        System.out.println("文件存放路径已修改！");
                    } else System.out.println("操作已取消");
                    break;
                case "opt6":
                    System.out.print("请输入目标学生学号：");
                    int studentId = Integer.parseInt(input.readLine());
                    Socket socket = getStudentSockets(studentId);
                    if (socket == null) {
                        System.out.println("该学生不存在！");
                    } else {
                        PrintStream socketOutput = new PrintStream(socket.getOutputStream());//学生输出
                        socketOutput.println(JSON.toJSONString(new Message("Monitor", "")));
                        System.out.println("正在截图...");
                    }
                    break;
                case "opt7"://导入新的学生
                    System.out.print("请输入班级名：");
                    str = input.readLine();
                    if (isTableExist(str)) {
                        System.out.println("该班级已存在！");
                        System.out.println("[Opt0]覆盖原有班级  [Opt1]在班级末尾新增  [Opt2]取消操作");
                        String tmp = input.readLine().toLowerCase();
                        if (tmp.equals("opt0")) {
                            createNewClass(str);
                            System.out.println("原班级已被覆盖");
                        } else if (tmp.equals("opt2")) {
                            System.out.println("操作已取消");
                            break;
                        }
                    } else {
                        createNewClass(str);
                        System.out.println("已为班级 " + str + " 建立新的数据库");
                    }

                    System.out.println("请依次输入学生的学号和姓名，学号和姓名之间由空格隔开" +
                            "，支持批量插入，请不要有空行，输入完成时请在新的一行输入 \"end\"");

                    List<Student> list = new ArrayList<>();
                    try {
                        int cnt = 0;
                        for (; ; ) {
                            String student = input.readLine();
                            if (student.equals("end")) break;

                            cnt += 1;
                            String arr[] = student.split("\\s+");
                            list.add(new Student(Integer.parseInt(arr[0]), "\"" + arr[1] + "\""));
                        }
                        if (list.isEmpty()) {
                            System.out.println("[警告]新建班级为空");
                        } else {
                            insertNewStudents(str, list);
                            System.out.println("插入数据完成！一共新增 " + cnt + " 个学生");
                        }
                    } catch (Exception e){
                        System.out.println("[警告]输入格式非法！");
                    }
                    break;
                case "opt8": //删除班级
                    printTableName();
                    System.out.print("请输入要删除的班级：");
                    str = input.readLine();
                    if (isTableExist(str)) {
                        deleteOldClass(str);
                    } else {
                        System.out.println("目标班级不存在！");
                    }
                    break;
                case "opt9": //收发作业
                    System.out.println("请输入作业");
                    //createNewTest ();
                    break;
                default:
                    System.out.println("操作无效，请重新输入");
                    break;
            }
        }
    }
}