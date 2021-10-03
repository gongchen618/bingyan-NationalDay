package client;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientService {

    public static String getDate (){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        return sdf.format(new Date());
    }
}
