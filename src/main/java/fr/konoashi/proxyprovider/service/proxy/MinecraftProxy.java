package fr.konoashi.proxyprovider.service.proxy;

import fr.konoashi.proxyprovider.service.proxy.network.PacketDirection;
import fr.konoashi.proxyprovider.service.proxy.network.ProtocolState;
import fr.konoashi.proxyprovider.service.handlers.NetworkPacketHandler;
import fr.konoashi.proxyprovider.service.handlers.NetworkPacketSizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.tinylog.Logger;

public class MinecraftProxy {

    private final int listenPort;
    private final String targetHost;
    private final int targetPort;

    private EventLoopGroup group;
    public ProtocolState state = ProtocolState.HANDSHAKING;
    private Session session;

    public MinecraftProxy(int listenPort, String targetHost, int targetPort) {
        this.listenPort = listenPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }


    public void run() {
        Logger.info("Starting proxy...");

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            session = new Session(MinecraftProxy.this, ch);
                            System.out.println("client channel: " + ch.toString());
                            p.addLast("sizer", new NetworkPacketSizer());
                            //p.addLast("codec", new NetworkPacketCodec()); to use when multiple versions of the protocol will be supported
                            p.addLast("handler", new NetworkPacketHandler(MinecraftProxy.this, session, PacketDirection.SERVERBOUND));
                            session.connectServer();
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(this.listenPort).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

}
