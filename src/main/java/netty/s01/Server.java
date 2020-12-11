package netty.s01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/9 14:13
 * @descrption 服务器
 */
@Slf4j
public class Server {
    //默认的线程处理通道组上面的事件
    public static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static void main(String[] args) throws IOException {
        //只负责连接的线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //辅助处理业务的线程池
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            log.info(String.valueOf(Thread.currentThread().getId()));

                            ChannelPipeline pl = ch.pipeline();
                            pl.addLast(new ServerChildHandler());
                        }
                    })
                    .bind(8888)
                    .sync();
            log.info("我的Netty开始服务");
            //关闭程序
            f.channel().closeFuture().sync();//close()->ChannelFuture
            log.info("server stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

@Slf4j
class ServerChildHandler extends ChannelInboundHandlerAdapter {//SimpleChannelInboundHandler Codec

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(String.valueOf(Thread.currentThread().getId()));
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        int oldCount=0;
        try {
            buf = (ByteBuf) msg;
            log.info(String.valueOf(buf));
            oldCount=buf.refCnt();
            log.info(String.valueOf(buf.refCnt()));
            byte[] bytes=new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            log.info(new String(bytes));
            //回写并关闭
            Server.clients.writeAndFlush(buf);
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
