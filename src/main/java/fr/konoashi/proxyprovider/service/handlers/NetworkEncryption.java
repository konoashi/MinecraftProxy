package fr.konoashi.proxyprovider.service.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class NetworkEncryption extends ByteToMessageCodec<ByteBuf> {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    private byte[] heapIn = new byte[0];
    private byte[] heapOut = new byte[0];


    public NetworkEncryption(SecretKey sharedSecret) throws GeneralSecurityException {
        this.encryptCipher = createCipher(sharedSecret, Cipher.ENCRYPT_MODE);
        this.decryptCipher = createCipher(sharedSecret, Cipher.DECRYPT_MODE);
    }

    private static Cipher createCipher(SecretKey sharedSecret, int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        return cipher;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf, ByteBuf out) throws Exception {
        int inLength = buf.readableBytes();
        this.readHeapIn(buf);

        int outLength = encryptCipher.getOutputSize(inLength);
        if(heapOut.length < outLength)
            this.heapOut = new byte[outLength];

        out.writeBytes(heapOut, 0, encryptCipher.update(heapIn, 0, inLength, heapOut));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int inLength = buf.readableBytes();
        this.readHeapIn(buf);

        int outLength = decryptCipher.getOutputSize(inLength);
        ByteBuf bufOut = ctx.alloc().heapBuffer(outLength);
        bufOut.writerIndex(this.decryptCipher.update(heapIn, 0, inLength, bufOut.array(), bufOut.arrayOffset()));
        out.add(bufOut);
    }

    private void readHeapIn(ByteBuf buf) {
        int length = buf.readableBytes();
        if(heapIn.length < length)
            this.heapIn = new byte[length];
        buf.readBytes(this.heapIn, 0, length);
    }

}
