package netty.s12;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/11 11:04
 * @descrption
 */
@Slf4j
public class Client {

    private Channel channel =null;
    public void connect() {
        //线程池
        EventLoopGroup group = new NioEventLoopGroup();
        //辅助启动类
        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f =
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ClientChannelInitializer())
                            .connect("localhost", 8888)
                            //连接监听器
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (future.isSuccess()) {
                                        log.info("connected");
                                        channel=future.channel();
                                    } else {
                                        log.error("not connected");
                                    }
                                }
                            })
                            .sync();

            f.channel().closeFuture().sync();//close()->ChannelFuture
            log.info("client connect stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    public void send(String msg){
        ByteBuf buf=Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }
    public void closeConnect(){
        this.send("_bye_");
    }
}

//初始化客户端channel
class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientHandler());
    }

}

@Slf4j
class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel 第一次脸上可用，写出一个字符串Direct Memory
        ByteBuf buf = Unpooled.copiedBuffer("法海你不懂爱".getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        int oldCount = 0;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String msgAccepted =new String(bytes);
            log.info(msgAccepted);
            ClientFrame.getInstance().updateText(msgAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (buf != null && oldCount == 0) {
                ReferenceCountUtil.release(buf);
            }
        }
    }

}
