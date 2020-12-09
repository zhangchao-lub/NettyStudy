package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/9 14:03
 * @descrption 客户端
 */
@Slf4j
public class Client {
    public static void main(String[] args) {
        //线程池
        EventLoopGroup group = new NioEventLoopGroup();
        //辅助启动类
        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f=
            b.group(group)
                    .channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            //TODO
//                        }
//                    })
                    //自定义handler
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8888);
//                    .addListener(null)
//                    .sync();
            //连接监听器
            f.addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        log.info("connected");
                    }else {
                        log.error("not connected");
                    }
                }
            });
            f.sync();
            log.info("。。。");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            group.shutdownGracefully();
        }
    }
}
@Slf4j
class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //TODO
        log.info(ch+"来了来了");
    }
}