package s01.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/7 17:10
 * @descrption
 */
@Slf4j
public class PoolServer {
    ExecutorService pool = Executors.newFixedThreadPool(50);

    /** 初始化大管家*/
    private Selector selector;
    //中文测试

    public static void main(String[] args) throws IOException {
        PoolServer server = new PoolServer();
        server.initServer(8888);
        server.listen();
    }

    private void initServer(int port) throws IOException {
        //获取管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //取消阻塞
        serverSocketChannel.configureBlocking(false);
        //绑定地址
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //打开选择器（大管家）
        selector = Selector.open();
        //管道上面注册大管家 关注accept:申请连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("服务器启动成功");
    }

    private void listen() throws IOException {
        //轮询访问selector
        while (true) {
            //选择所有可进行IO操作的key
            selector.select();
            // 遍历
            Iterator ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                //移除key
                ite.remove();
                //IO操作
                if (key.isAcceptable()) {
                    //获取通道
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //
                    SocketChannel channel = server.accept();
                    //
                    channel.configureBlocking(false);
                    //
                    channel.register(selector, SelectionKey.OP_READ);
                    log.info(channel.getRemoteAddress()+":请进房间");
                    //
                } else if (key.isReadable()) {
                    //移除
                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                    //执行Read
                    pool.execute(new ThreadHandlerChannel(key));
                }

            }
        }
    }

}

@Slf4j
class ThreadHandlerChannel extends Thread {
    private SelectionKey key;

    ThreadHandlerChannel(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        //
        SocketChannel channel= (SocketChannel) key.channel();
        //
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        //
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try{
            log.info(channel.getRemoteAddress()+":开始服务");
            int size=0;
            while((size=channel.read(buffer))>0){
                //
                buffer.flip();
                //
                log.info(channel.getRemoteAddress()+new String(buffer.array(), 0, size));
                baos.write(buffer.array(),0,size);
                //
                buffer.clear();

//                int len = channel.read(buffer);
//
//                if (len != -1) {
//                    log.info(new String(buffer.array(), 0, len));
//                }
//
//                ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
//                channel.write(bufferToWrite);
            }
            baos.close();
            //
            byte[] content=(channel.getRemoteAddress()+"服务完成").getBytes();
            ByteBuffer writeBuf=ByteBuffer.allocate(content.length);
            writeBuf.put(content);
            writeBuf.flip();
            channel.write(writeBuf);
            if(size==-1){
                channel.close();
            }else{
                key.interestOps(key.interestOps()|SelectionKey.OP_READ);
                key.selector().wakeup();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
