package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
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
            ChannelFuture f =
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            //方法一:
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            //TODO
//                        }
//                    })
                            //方法二:自定义handler
                            .handler(new ClientChannelInitializer())
                            .connect("localhost", 8888);
//                    .addListener(null)
//                    .sync();
            //连接监听器
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("connected");
                    } else {
                        log.error("not connected");
                    }
                }
            });
            f.sync();
            f.channel().closeFuture().sync();//close()->ChannelFuture
            log.info("client connect stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

@Slf4j
class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChildHandler());
    }

    private class ChildHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = null;
            int oldCount = 0;
            try {
                buf = (ByteBuf) msg;
                oldCount=buf.refCnt();
                log.info(String.valueOf(buf));
                log.info(String.valueOf(buf.refCnt()));
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(), bytes);
                log.info(new String(bytes));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (buf != null && oldCount==0) {
                    ReferenceCountUtil.release(buf);
                    log.info(String.valueOf(buf.refCnt()));
                }
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //channel 第一次连上可用 ，写出一个字符串Direct Memory
            ByteBuf buf = Unpooled.copiedBuffer("Hello".getBytes());
            ctx.writeAndFlush(buf);
        }
    }
}