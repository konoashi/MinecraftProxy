package fr.konoashi.proxyprovider.service.handlers;

import fr.konoashi.proxyprovider.service.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.Arrays;
import java.util.List;

public class NetworkPacketCodec extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.duplicate().readBytes(bytes);
        System.out.println(Arrays.toString(bytes));
        int packetId = NetworkUtils.readVarInt(in);
    }
}
