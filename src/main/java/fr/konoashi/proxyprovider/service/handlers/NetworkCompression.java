package fr.konoashi.proxyprovider.service.handlers;

import fr.konoashi.proxyprovider.service.utils.NetworkUtils;
import fr.konoashi.proxyprovider.service.proxy.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class NetworkCompression extends ByteToMessageCodec<ByteBuf> {

    private final Session session;

    private final byte[] deflateBuf = new byte[8192];
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();

    public NetworkCompression(Session session) {
        this.session = session;
    }

    private int getThreshold() {
        return session.getCompressionThreshold();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int readable = in.readableBytes();
        if(readable < this.getThreshold()) {
            NetworkUtils.writeVarInt(out, 0);
            out.writeBytes(in);
            return;
        }

        byte[] bytes = new byte[readable];
        in.readBytes(bytes);
        NetworkUtils.writeVarInt(out, readable);

        deflater.setInput(bytes, 0, readable);
        deflater.finish();
        while(!deflater.finished()) {
            int length = deflater.deflate(deflateBuf);
            out.writeBytes(deflateBuf, 0, length);
        }
        deflater.reset();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int size = NetworkUtils.readVarInt(buf);
        int readable = buf.readableBytes();
        if(size == 0) {
            out.add(buf.readBytes(readable));
            return;
        }

        byte[] compressed = new byte[readable];
        buf.readBytes(compressed);

        inflater.setInput(compressed);
        byte[] decompressed = new byte[size];
        inflater.inflate(decompressed);
        out.add(Unpooled.wrappedBuffer(decompressed));
        inflater.reset();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        deflater.end();
        inflater.end();
    }

}
