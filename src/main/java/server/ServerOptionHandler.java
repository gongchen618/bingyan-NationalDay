package server;

import com.alibaba.fastjson.JSON;
import message.Message;

import java.io.*;
import java.net.Socket;

import static server.ServerCore.*;
import static server.ServerSqlHandler.*;

public class ServerOptionHandler extends Thread {

    private void printOptionList (){
        System.out.println("[Opt0]修改当前班级  " +
                "[Opt1]重置/修改学生状态  " +
                "[Opt2]标记上课/下课  " +
                "[Opt3]查看课堂情况  " +
                "[Opt4]向学生发送消息");
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


        for (;;) {
            printOptionList();
            String str = input.readLine().toLowerCase();
            switch (str) {
                case "opt0": //修改当前班级
                    System.out.print("请输入新的班级：");
                    str = input.readLine();
                    if (!isTableExist (str)) {
                        System.out.println("该班级不存在！");
                        break;
                    }
                    setClassId(str);
                    System.out.println("当前班级：" + getClassId());
                    break;
                case "opt1": //修改学生状态
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]重置所有学生状态为 Absent  [Opt1]修改单个学生状态  [Opt2]取消操作");

                        str = input.readLine().toLowerCase();
                        flag = true;

                        if (str.equals("opt0")) resetStatus();
                        else if (str.equals("opt1")) {
                            System.out.println("请输入学生学号：");
                            int studentId = Integer.parseInt(input.readLine());
                            System.out.println("请输入新的状态：");
                            str = input.readLine();
                            changeStudentStatus(studentId, str);
                        } else if (str.equals("opt2")) System.out.println("操作已取消");
                        else {
                            System.out.println(str + " 操作无效，请重新输入");
                            flag = false;
                        }
                    }
                    break;
                case "opt2": //标记上课下课
                    for (boolean flag = false; !flag; ) {
                        System.out.println("[Opt0]标记当前已上课  [Opt1]标记当前已下课  [Opt2]取消操作");
                        System.out.println("当前上课状态：" + isFlagIsClassTime());

                        str = input.readLine();
                        flag = true;

                        if (str.equals("opt0")) setFlagIsClassTime(true);
                        else if (str.equals("opt1")) setFlagIsClassTime(false);
                        else if (str.equals("opt2")) System.out.println("操作已取消");
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
                    break;
                default:
                    System.out.println("操作无效，请重新输入");
                    break;
            }
        }
    }
}