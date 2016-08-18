package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2  of <i>Netty in Action</i>
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)      // 创建 ServerBootstrap
                .channel(NioServerSocketChannel.class)  // 指定使用 NIO 的传输 Channel
                .localAddress(new InetSocketAddress(port))  // 设置 socket 地址使用所选的端口
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });

            ChannelFuture f = b.bind().sync();  // 绑定的服务器;sync 等待服务器关闭
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            f.channel().closeFuture().sync();   // 关闭 channel 和 块，直到它被关闭
        } finally {
            group.shutdownGracefully().sync();  // 关闭的 EventLoopGroup，释放所有资源
        }
    }
}
