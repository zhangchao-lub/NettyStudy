package s01.io.nio;

import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/7 16:29
 * @descrption 管道 大管家 全双工
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //管道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8888));
        //设置阻塞态为false
        ssc.configureBlocking(false);

        log.info("server started, listening on :" + ssc.getLocalAddress());
        //大管家
        Selector selector = Selector.open();
        //管道上面注册大管家 关注accept:申请连接事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        log.info("****期待您的光临");
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                log.info("安排");
                SelectionKey key = it.next();
                it.remove();
                handle(key);
            }
        }
    }

    private static void handle(SelectionKey key) {
        if (key.isAcceptable()) {
            //获取管道
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                //设置阻塞态为false
                sc.configureBlocking(false);

                log.info(sc.getRemoteAddress()+":请进房间");
                sc.register(key.selector(), SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (key.isReadable()) {
            SocketChannel sc = null;
            try {
                sc = (SocketChannel) key.channel();
                log.info(sc.getRemoteAddress()+":开始服务");
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();
                int len = sc.read(buffer);

                if (len != -1) {
                    log.info(new String(buffer.array(), 0, len));
                }

                ByteBuffer bufferToWrite = ByteBuffer.wrap((sc.getRemoteAddress()+"服务完成").getBytes());
                sc.write(bufferToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (sc != null) {
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
