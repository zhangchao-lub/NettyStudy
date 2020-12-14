package netty.s13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/14 17:48
 * @descrption 解码器
 */
public class TankMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<8){
            return;
        }
        in.markReaderIndex();

        int x=in.readInt();
        int y=in.readInt();
        out.add(new TankMsg(x,y));
    }
}
