package s01.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/7 15:58
 * @descrption 半双工通信
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss=new ServerSocket();
        ss.bind(new InetSocketAddress("127.0.0.1",8888));
        while (true){
            Socket s=ss.accept();//阻塞方法

            new Thread(()->{
               handle(s);
            }).start();
        }
    }

    static void handle(Socket s) {
        try {
            byte[] bytes=new byte[1024];
            int len=s.getInputStream().read(bytes);
            log.info(new String(bytes, 0, len));

//            s.getOutputStream().write(bytes, 0, len);
//            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
