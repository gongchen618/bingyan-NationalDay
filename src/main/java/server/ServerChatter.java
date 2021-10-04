package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static server.ServerGetSql.*;


public class ServerChatter {
    private static boolean flagIsClassTime = false;

    public static boolean isFlagIsClassTime() {
        return flagIsClassTime;
    }

    public static void setFlagIsClassTime(boolean flag) {
        flagIsClassTime = flag;
        System.out.println("当前上课状态已修改为：" + flagIsClassTime);
    }

    private static String getOpt () {
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

    static public String classId = "class1";

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);

        System.out.println("教师端已启动");
        System.out.println("教师端：" + server.getInetAddress() + ":" + server.getLocalPort());

        OptionChoose optionChoose = new OptionChoose();
        optionChoose.start();

        for(;;) {
            Socket client = server.accept();
            StudentStatusHandler studentStatusHandler = new StudentStatusHandler(client);
            studentStatusHandler.start();
        }
    }

    private static class OptionChoose extends Thread {

        private void printOptionList (){
            System.out.println("[Opt0]修改当前班级  [Opt1]重置/修改学生状态  [Opt2]标记上课/下课  [Opt3]查看课堂情况");
        }

        public OptionChoose() {
        }

        @Override
        public void run() {
            super.run();

            for (;;) {
                printOptionList();
                String str = getOpt();
                switch (str) {
                    case "opt0":
                        System.out.println("请输入新的班级");
                        setClassId(getOpt());
                        System.out.println("当前班级：" + getClassId());
                        break;
                    case "opt1":
                        for (boolean flag = false; !flag; ) {
                            System.out.println("[Opt0]重置所有学生状态为 Absent  [Opt1]修改单个学生状态  [Opt2]取消操作");

                            str = getOpt();
                            flag = true;

                            if (str.equals("opt0")) resetStatus();
                            else if (str.equals("opt1")) {
                                System.out.println("请输入学生学号：");
                                int studentId = Integer.parseInt(getOpt());
                                System.out.println("请输入新的状态：");
                                str = getOpt();
                                changeStudentStatus(studentId, str);
                            } else if (str.equals("opt2")) System.out.println("操作已取消");
                            else {
                                System.out.println(str + " 操作无效，请重新输入");
                                flag = false;
                            }
                        }
                        break;
                    case "opt2":
                        for (boolean flag = false; !flag; ) {
                            System.out.println("[Opt0]标记当前已上课  [Opt1]标记当前已下课  [Opt2]取消操作");
                            System.out.println("当前上课状态：" + isFlagIsClassTime());

                            str = getOpt();
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
                    case "opt3":
                        printStudentListByOrder();
                        break;
                    default:
                        System.out.println(str + " 操作无效，请重新输入");
                        break;
                }
            }
        }
    }
}