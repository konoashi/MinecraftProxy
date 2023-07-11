package fr.konoashi.proxyprovider.service.proxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.konoashi.proxyprovider.service.proxy.network.LazyLoadBase;
import fr.konoashi.proxyprovider.service.proxy.network.PacketDirection;
import fr.konoashi.proxyprovider.service.proxy.network.ProtocolState;
import fr.konoashi.proxyprovider.service.handlers.NetworkCompression;
import fr.konoashi.proxyprovider.service.handlers.NetworkEncryption;
import fr.konoashi.proxyprovider.service.handlers.NetworkPacketHandler;
import fr.konoashi.proxyprovider.service.handlers.NetworkPacketSizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.tinylog.Logger;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

public class Session {

    private final MinecraftProxy proxy;
    private final Channel clientChannel;
    private Channel serverChannel;

    private ProtocolState state = ProtocolState.HANDSHAKING;
    private int compressionThreshold = -1;

    private String username;
    //private Account account;

    public Session(MinecraftProxy proxy, Channel channel) {
        this.proxy = proxy;
        this.clientChannel = channel;
    }

    public void connectServer() {
        System.out.println(proxy.getTargetHost());
        LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
            protected NioEventLoopGroup load() {
                return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
            }
        };

        LazyLoadBase<io.netty.channel.epoll.EpollEventLoopGroup> field_181125_e = new LazyLoadBase<EpollEventLoopGroup>() {
            protected EpollEventLoopGroup load() {
                return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
            }
        };
        Class oclass;
        LazyLoadBase lazyloadbase;
        if (Epoll.isAvailable()) {
            oclass = EpollSocketChannel.class;
            lazyloadbase = field_181125_e;
        } else {
            oclass = NioSocketChannel.class;
            lazyloadbase = CLIENT_NIO_EVENTLOOP;
        }


        (new Bootstrap()).group((EventLoopGroup)lazyloadbase.getValue()).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel ch) throws Exception {
                try {
                    ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException exception) {
                    exception.printStackTrace();
                }
                serverChannel = ch;
                System.out.println("server channel: " + ch.toString());
                ch.pipeline()
                        .addLast("sizer", new NetworkPacketSizer())
                        .addLast("handler", new NetworkPacketHandler(proxy, Session.this, PacketDirection.CLIENTBOUND));
            }
        }).channel(oclass).connect(proxy.getTargetHost(), proxy.getTargetPort()).syncUninterruptibly();
        /*EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    serverChannel = ch;
                    ch.pipeline()
                            .addLast("sizer", new NetworkPacketSizer())
                            .addLast("handler", new NetworkPacketHandler(proxy, Session.this, PacketDirection.CLIENTBOUND));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(proxy.getTargetHost(), proxy.getTargetPort()).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }*/
        System.out.println("Connected to server");
        Logger.info("Connected to server");
        proxy.state = ProtocolState.HANDSHAKING;
    }

    public void disconnect() {
        this.disconnectServer();
        this.disconnectClient();
    }

    public void disconnectClient() {
        if(clientChannel != null) {
            try {
                clientChannel.close().sync();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void disconnectServer() {
        if(serverChannel != null) {
            try {
                serverChannel.close().sync();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            serverChannel = null;
        }
    }

    private void closeChannel(Channel channel) {
        if(channel == null)
            return;

        try {
            channel.close().sync();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void setCompressionThreshold(int compressionThreshold) {
        Logger.info("Compression threshold set to {}", compressionThreshold);
        this.compressionThreshold = compressionThreshold;

        // At the moment, compression is only enabled for transport between the server and proxy.
        // There is no compression between the proxy and client.
        if(compressionThreshold >= 0) {
            this.addCompression(serverChannel);
        } else {
            this.removeCompression(serverChannel);
        }
    }

    private static final String COMPRESSION_HANDLER_NAME = "compression";
    private static final String ENCRYPTION_HANDLER_NAME = "encryption";

    private void addCompression(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if(pipeline.get(COMPRESSION_HANDLER_NAME) == null)
            pipeline.addBefore("codec", COMPRESSION_HANDLER_NAME, new NetworkCompression(this));
    }

    private void removeCompression(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if(pipeline.get(COMPRESSION_HANDLER_NAME) != null)
            pipeline.remove(COMPRESSION_HANDLER_NAME);
    }

    public void enableEncryption(SecretKey sharedSecret) {
        try {
            serverChannel.pipeline().addBefore("sizer", ENCRYPTION_HANDLER_NAME, new NetworkEncryption(sharedSecret));
            Logger.info("Enabled encryption");
        } catch (GeneralSecurityException ex) {
            Logger.error(ex, "Failed to enable encryption");
        }
    }

    public void sendToClient(ByteBuf packet) {
        if(clientChannel.isWritable()) {
            try {
                clientChannel.writeAndFlush(packet).sync();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void sendToServer(ByteBuf packet) {
        if(serverChannel.isWritable()) {
            try {
                serverChannel.writeAndFlush(packet).sync();
            } catch (Exception err){
                err.printStackTrace();
            }
        }
    }

    /*public void setUsername(String username) {
        this.username = username;
        this.account = AuthenticationHandler.getInstance().getByUsername(username);
    }*/

    public Channel getClientChannel() {
        return clientChannel;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public ProtocolState getState() {
        return state;
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public String getUsername() {
        return username;
    }

    /*public Account getAccount() {
        return account;
    }*/

    public void setState(ProtocolState state) {
        Logger.info("State transitioned: {}", state.name());
        this.state = state;
    }

}
