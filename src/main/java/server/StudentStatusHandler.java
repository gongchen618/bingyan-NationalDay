package server;

import com.alibaba.fastjson.JSON;
import server.sql.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import static server.ServerChatter.isFlagIsClassTime;
import static server.ServerGetSql.changeStudentStatus;
import static server.ServerGetSql.getStudentById;

public class StudentStatusHandler extends Thread{
    private Socket socket;
    private Student studentInfo = null;

    StudentStatusHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("新学生端启动：" + socket.getInetAddress() + ":" + socket.getPort());

        try{
            PrintStream socketOutput = new PrintStream(socket.getOutputStream());
            BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /*
                socketOutput.println("");//向学生端输出
                String str = socketInput.readLine();//从学生端输入
                 */

            boolean flagConnect = false;

            do {
                String str = socketInput.readLine();
                Student student = JSON.parseObject(str, Student.class);
                //System.out.println(student.toString());

                studentInfo = getStudentById(student.getStudentId());
                //System.out.println(studentInfo.toString());

                //学生端的登录操作
                if (studentInfo == null) {
                    socketOutput.println("学号不存在！");
                } else if (studentInfo.getPassword().equals(student.getPassword())) {
                    flagConnect = true;
                    System.out.println(studentInfo.getName() + " 登录成功！");
                    if (isFlagIsClassTime() == true) {
                        changeStudentStatus(student.getStudentId(), "Late");
                        socketOutput.println("Login Successfully");
                        socketOutput.println("status : Late");
                    } else {
                        changeStudentStatus(student.getStudentId(), "Normal");
                        socketOutput.println("Login Successfully");
                        socketOutput.println("status : Normal");
                    }
                } else {
                    socketOutput.println("密码错误！");
                }

            } while (!flagConnect);


            int num = -1;
            do {
                num = socketInput.read();
                try {
                    Thread.sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } while (num > 0);

            socketInput.close();
            socketOutput.close();

                /*
                do{
                    String str = socketInput.readLine();//客户端输入
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        socketOutput.println("bye");
                    } else {
                        System.out.println(str);
                        socketOutput.println("接受到文件大小：" + str.length());
                    }

                } while (flag);
                */

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("学生端连接异常断开："+ socket.getInetAddress() + ":" + socket.getPort());
        } finally {
            try {
                socket.close();
                System.out.println("学生端断开："+ socket.getInetAddress() + ":" + socket.getPort());
                if (studentInfo != null) {
                    System.out.println(studentInfo.getName() + " 已下线");
                    if (isFlagIsClassTime() == true) {
                        changeStudentStatus(studentInfo.getStudentId(), "LeaveEarly");//这里可能有点问题
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}