package netty;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/9 14:13
 * @descrption 服务器
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss=new ServerSocket();
        ss.bind(new InetSocketAddress(8888));

        Socket s=ss.accept();
        log.info("来客人了");
    }
}
