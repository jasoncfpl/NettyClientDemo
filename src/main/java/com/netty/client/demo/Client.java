package com.netty.client.demo;

import com.netty.client.demo.decoder.MyDelimiterFrameDecoder;
import com.netty.client.demo.handler.ClientDecoder;
import com.netty.client.demo.handler.ClientEncoder;
import com.netty.client.demo.handler.ClientHandler;
import com.netty.client.demo.handler.ClientIdleStateHandler;
import com.netty.client.demo.model.PkgDataBean;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final String TAG = "Client";
    private final int PORT = 7010;
    //连接的服务端ip地址
    private final String IP = "127.0.0.1";

    public static void main(String[] args) {
        getInstance().connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    String s = "hello netty";
                    PkgDataBean pkgDataBean = new PkgDataBean();
                    pkgDataBean.setCmd((byte) 0x01);
                    pkgDataBean.setData(s);
                    pkgDataBean.setDataLength((byte) pkgDataBean.getData().getBytes().length);
                    getInstance().getChannel().writeAndFlush(pkgDataBean);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }


    private Channel channel;
    private Bootstrap bootstrap;
    private static Client client;

    public void connect() {
        try {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap().channel(NioSocketChannel.class)
                    .group(nioEventLoopGroup)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //解决粘包
//                            pipeline.addLast("delimiter-decoder",new MyDelimiterFrameDecoder(65535));
                            //编码
                            pipeline.addLast("encoder",new ClientEncoder());
                            //解码
                            pipeline.addLast("decoder",new ClientDecoder());
                            //handler
                            pipeline.addLast("handler",new ClientHandler(client));

//                            pipeline.addLast(new ClientIdleStateHandler(10, 0, 0));
                            pipeline.addFirst("idle",new IdleStateHandler(10, 0, 0));

                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(IP,PORT).sync();
            channelFuture.addListener(new ConnStateListener(this));
            channel = channelFuture.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * 重连
     */
    public void reConnect() {
        try {
            System.out.println(TAG + "发起重连");
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(IP, PORT));
            channel = channelFuture.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class ConnStateListener implements ChannelFutureListener {
        private Client client;

        public ConnStateListener(Client client) {
            this.client = client;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            System.out.println(TAG + "ChannelFuture operationComplete ：" + future.isSuccess());
            if (future.isSuccess()) {
                System.out.println(TAG + "ChannelFuture 连接成功");
            } else {
                System.out.println(TAG + "ChannelFuture 连接失败");
                EventLoop loop = future.channel().eventLoop();
                loop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        client.reConnect();
                    }
                },5, TimeUnit.SECONDS);

            }
        }
    }

}
