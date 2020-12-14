package netty.s13;

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
        ch.pipeline()
                .addLast(new TankMsgEncoder())
                .addLast(new TankMsgDecoder())
                .addLast(new ClientHandler());
    }

}

@Slf4j
class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new TankMsg(5,8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        int oldCount = 0;
        try {
            TankMsg tm= (TankMsg) msg;
            ClientFrame.getInstance().updateText(tm.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (buf != null && oldCount == 0) {
                ReferenceCountUtil.release(buf);
            }
        }
    }

}
