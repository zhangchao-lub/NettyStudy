package s01.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/9 9:59
 * @descrption
 */
@Slf4j
public class MoreClient {
    public static void main(String[] args) throws Exception {
        for(int i=0;i<50;i++){
            new Thread(()->{
                try {
                    play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
//            Thread.sleep(100);
        }
    }

    private static void play() throws IOException {
        Socket s = new Socket("127.0.0.1", 8888);
        s.getOutputStream().write("来个泰式按摩".getBytes());
        s.getOutputStream().flush();
        log.info("write over , waiting for msg back");
        byte[] bytes = new byte[1024];
        int len = s.getInputStream().read(bytes);
        log.info(new String(bytes, 0, len));
        s.close();
    }
}
