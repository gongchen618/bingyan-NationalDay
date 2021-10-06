package server;

import server.sql.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import static server.ServerCore.*;
import static server.ServerSqlHandler.*;

public class StudentMessageReceiver extends Thread{
    private Socket socket;
    private Student studentInfo = null;

    StudentMessageReceiver(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        //System.out.println("新学生端启动：" + socket.getInetAddress() + ":" + socket.getPort());
        try {
            option();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(studentInfo.getIdAndName() + " 的连接断开"); //其实也可能不是异常
            //System.out.println("学生端连接异常断开："+ socket.getInetAddress() + ":" + socket.getPort());
        }

        try {
            setStudentSockets(studentInfo.getStudentId(), null);
            socket.close();
            //System.out.println("学生端断开："+ socket.getInetAddress() + ":" + socket.getPort());

            if (studentInfo != null) {
                changeStudentStatus(studentInfo.getStudentId(), "Offline");
                studentInfo = getStudentById(studentInfo.getStudentId()); //更新一下

                if (getClassTime() == 0) {
                    changeStudentPerformance(studentInfo.getStudentId(), "缺席");
                } else if (getClassTime() == 1) { //这里默认不存在早退 + 迟到的状态...
                        changeStudentPerformance(studentInfo.getStudentId(), "早退");
                };//2: 状态不变
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void option() throws IOException {

        PrintStream socketOutput = new PrintStream(socket.getOutputStream());//向学生输出
        BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));//学生输入

        boolean flagConnect = false;

        do {//学生端登录

            try {
                String str = socketInput.readLine();
                String password = socketInput.readLine();

                int studentId = Integer.parseInt(str);
                studentInfo = getStudentById(studentId);

                if (studentInfo == null) {
                    socketOutput.println("学号不存在！");
                } else if (studentInfo.getPassword().equals(password)) {
                    flagConnect = true;
                    setStudentSockets(studentInfo.getStudentId(), socket);
                    changeStudentStatus(studentId, "Online");

                    if (getClassTime() == 0) {
                        changeStudentPerformance(studentId, "正常");
                        socketOutput.println("登录成功！课程未开始，请不要登出");
                    } else if (getClassTime() == 1) {
                        changeStudentPerformance(studentId, "迟到");
                        socketOutput.println("登录成功！课程已经开始，你迟到了");
                    } else {
                        socketOutput.println("登录成功！课程已结束，状态不再更新");
                    }
                } else {
                    socketOutput.println("密码错误！");
                }
            } catch (Exception e){
                socketOutput.println("非法输入！");
            }
        } while (!flagConnect);

        String str = null; //心跳包，以及只有这里会直接接收学生端的消息
        do {
            str = socketInput.readLine();
            socketOutput.println("");
            if (str != null && str.length() > 0) {
                StudentMessageHandler studentMessageHandler =
                        new StudentMessageHandler(studentInfo.getStudentId(), str);
                studentMessageHandler.option();
            }
        } while (str != null);

        socketInput.close();
        socketOutput.close();
    }
}