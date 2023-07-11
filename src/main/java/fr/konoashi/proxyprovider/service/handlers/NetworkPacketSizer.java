package fr.konoashi.proxyprovider.service.handlers;

import fr.konoashi.proxyprovider.service.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

public class NetworkPacketSizer extends ByteToMessageCodec<ByteBuf> {

    private static final int LENGTH_SIZE = 5;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int length = in.readableBytes();
        out.ensureWritable(LENGTH_SIZE + length);
        NetworkUtils.writeVarInt(out, length);
        out.writeBytes(in);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        buf.markReaderIndex();
        int length = NetworkUtils.readVarInt(buf);
        if(buf.readableBytes() < length) {
            buf.resetReaderIndex();
            return;
        }
        out.add(buf.readBytes(length));
    }

}
