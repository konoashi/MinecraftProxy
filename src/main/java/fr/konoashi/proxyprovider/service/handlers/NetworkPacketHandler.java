package fr.konoashi.proxyprovider.service.handlers;

import fr.konoashi.proxyprovider.App;
import fr.konoashi.proxyprovider.database.access.AccountAccess;
import fr.konoashi.proxyprovider.database.document.AccountDocument;
import fr.konoashi.proxyprovider.service.chat.ChatComponentText;
import fr.konoashi.proxyprovider.service.chat.EnumChatFormatting;
import fr.konoashi.proxyprovider.service.chat.IChatComponent;
import fr.konoashi.proxyprovider.service.proxy.MinecraftProxy;
import fr.konoashi.proxyprovider.service.proxy.Session;
import fr.konoashi.proxyprovider.service.proxy.network.PacketBuffer;
import fr.konoashi.proxyprovider.service.proxy.network.PacketDirection;
import fr.konoashi.proxyprovider.service.proxy.network.ProtocolState;
import fr.konoashi.proxyprovider.service.utils.AuthUtils;
import fr.konoashi.proxyprovider.service.utils.CryptUtil;
import fr.konoashi.proxyprovider.utils.console.ANSI;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.UUID;

public class NetworkPacketHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private MinecraftProxy client;
    private Session server;
    private PacketDirection direction;

    public NetworkPacketHandler(MinecraftProxy client, Session server, PacketDirection direction) {
        this.client = client;
        this.server = server;
        this.direction = direction;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //Act as a server for the real client
        byte[] bytes = new byte[msg.readableBytes()];
        msg.duplicate().readBytes(bytes);
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(bytes);
        PacketBuffer packetBuffer = new PacketBuffer(copiedBuffer);
        System.out.println( ANSI.ANSI_GREEN + this.direction.getName() + ": " + this.client.state.getDisplayName() + ": " +Arrays.toString(packetBuffer.array()) + ANSI.ANSI_RESET);
        int packetId = packetBuffer.readVarIntFromBuffer();

        if (this.direction == PacketDirection.SERVERBOUND) {
            if (this.client.state == ProtocolState.PLAY) {
                this.server.sendToServer(msg);
            } else if (this.client.state == ProtocolState.LOGIN) {
                if (packetId == 0x00) {
                    String username = packetBuffer.readStringFromBuffer(16);;
                    App.connector.setCollection("Sessions");
                    AccountDocument account = new AccountAccess(App.connector).getAccount(AuthUtils.uuidFromName(username).get());
                    if (account != null) {
                        loginSuccess();
                        this.server.sendToServer(msg);
                    } else {
                        ReferenceCountUtil.release(msg);
                        IChatComponent reason = new ChatComponentText(EnumChatFormatting.RED + "You must use an account added into our database, " + username + " is not one of them.");
                        loginDisconnect(reason);
                        this.server.disconnect();
                    }

                }
            } else if (this.client.state == ProtocolState.STATUS) {
                if (packetId == 0x00) {
                    this.server.sendToServer(msg);
                }
                if (packetId == 0x01) {
                    this.server.sendToServer(msg);
                }
            } else if (this.client.state == ProtocolState.HANDSHAKING) {
                if (packetId == 0x00) {
                    int protocolVersion = packetBuffer.readVarIntFromBuffer();
                    String ip = packetBuffer.readStringFromBuffer(250);
                    boolean hasFMLMarker = ip.contains("\u0000FML\u0000");
                    ip = ip.split("\u0000")[0];
                    int port = packetBuffer.readUnsignedShort();
                    int nextState = packetBuffer.readVarIntFromBuffer();
                    if (nextState == 1) {
                        this.client.state = ProtocolState.STATUS;
                        this.server.sendToServer(msg);
                    }
                    if (nextState == 2) {
                        this.client.state = ProtocolState.LOGIN;
                        this.server.sendToServer(msg);
                    }
                }
            }
        } else {
            if (this.client.state == ProtocolState.STATUS) {
                if (packetId == 0x00) {
                    this.server.sendToClient(msg);
                }
                if (packetId == 0x01) {
                    this.server.sendToClient(msg);
                    this.server.disconnect();
                }
            } else if (this.client.state == ProtocolState.LOGIN) {
                if (packetId == 0x00) {
                    ReferenceCountUtil.release(msg);
                    this.server.disconnect(); //that's the disconnect packet
                }
                if (packetId == 0x01) {
                    ReferenceCountUtil.release(msg);
                    //There we receive the encryption start packet and we need to send the encryption response and join session on mojang api
                    String serverId = packetBuffer.readStringFromBuffer(5);
                    byte[] publicKeyBytes = packetBuffer.readByteArray();
                    PublicKey publicKey = CryptUtil.readPublicKey(publicKeyBytes);
                    byte[] nonce = packetBuffer.readByteArray();
                    SecretKey sharedSecret = CryptUtil.generateSharedSecret();
                    String serverHash = AuthUtils.calculateServerHash(serverId, publicKey, sharedSecret);

                    encryptionResponse(publicKey, nonce, sharedSecret);
                    this.server.enableEncryption(sharedSecret);
                }
                if (packetId == 0x02) {

                }
                if (packetId == 0x03) {

                }
            } else {
                this.server.sendToClient(msg);
            }
        }

    }

    /*public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {

        if (throwable instanceof DecoderException) {
            DecoderException decoderException = ((DecoderException) throwable);
            System.out.println(ANSI.ANSI_RED + "Internal Decoder Exception: " + throwable + ANSI.ANSI_RESET);
        }

        if (throwable instanceof EncoderException) {
            EncoderException decoderException = ((EncoderException) throwable);
            System.out.println(ANSI.ANSI_RED + "Internal Encoder Exception: " + throwable + ANSI.ANSI_RESET);
        }

        if (throwable instanceof TimeoutException) {
            System.out.println(ANSI.ANSI_RED +  "Timeout: " + channelhandlercontext.name() + ANSI.ANSI_RESET);
        }
        else {
            System.out.println(ANSI.ANSI_RED +  "Internal Exception: " + throwable + ANSI.ANSI_RESET);
        }

        //this.server.disconnect();
    }*/

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.server.disconnect();
    }

    public void loginSuccess() {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeVarIntToBuffer(2);
        packet.writeUuid(new UUID(1266565, 484654));
        packet.writeString("username");
        this.server.sendToClient(packet);
    }

    public void encryptionResponse(PublicKey publicKey, byte[] nonce, SecretKey sharedSecret) {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeVarIntToBuffer(2);
        packet.writeByteArray(CryptUtil.encrypt(sharedSecret.getEncoded(), publicKey));
        packet.writeByteArray(CryptUtil.encrypt(nonce, publicKey));
        
        this.server.sendToServer(packet);
    }

    public void loginDisconnect(IChatComponent reason) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeVarIntToBuffer(0);
        packet.writeChatComponent(reason);
        System.out.println(Arrays.toString(buf.array()));

        this.server.sendToClient(packet);
    }
}
