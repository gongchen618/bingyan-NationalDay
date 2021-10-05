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

        System.out.println("新学生端启动：" + socket.getInetAddress() + ":" + socket.getPort());
        try {
            option();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("学生端连接异常断开："+ socket.getInetAddress() + ":" + socket.getPort());
        }

        try {
            setStudentSockets(studentInfo.getStudentId(), null);
            socket.close();
            System.out.println("学生端断开："+ socket.getInetAddress() + ":" + socket.getPort());

            if (studentInfo != null) {
                System.out.println(studentInfo.getIdAndName() + "已下线");
                if (isFlagIsClassTime() == true) {
                    changeStudentStatus(studentInfo.getStudentId(), "LeaveEarly");//这里可能有点问题
                }
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
                String str = socketInput.readLine(), password = socketInput.readLine();
                int studentId = Integer.parseInt(str);
                studentInfo = getStudentById(studentId);

                if (studentInfo == null) {
                    socketOutput.println("[F]学号不存在！");
                } else if (studentInfo.getPassword().equals(password)) {
                    flagConnect = true;
                    System.out.println(studentInfo.getIdAndName() + "已上线");
                    setStudentSockets(studentInfo.getStudentId(), socket);

                    if (isFlagIsClassTime() == true) {
                        changeStudentStatus(studentId, "Late");
                        socketOutput.println("[T]登录成功！但是迟到了");
                    } else {
                        changeStudentStatus(studentId, "Normal");
                        socketOutput.println("[T]登录成功！");
                    }
                } else {
                    socketOutput.println("[F]密码错误！");
                }
            } catch (Exception e){
                socketOutput.println("[F]非法输入！");
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