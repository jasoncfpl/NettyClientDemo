package com.netty.client.demo.handler;

import com.netty.client.demo.Client;
import com.netty.client.demo.model.PkgDataBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

public class ClientHandler extends SimpleChannelInboundHandler<PkgDataBean> {
    private static final String TAG = "ClientHandler";
    private Client client;
    public ClientHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PkgDataBean bean) throws Exception {
        System.out.println(TAG + "收到解码器处理过的数据：" + bean.toString());
        ctx.fireChannelRead(bean);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println(TAG + "channelActive：与服务端连接成功：" + ctx.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println(TAG + "channelInactive：与服务端断开连接：" + ctx.toString());
        reConnect(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println(TAG + "channelRegistered：" + ctx.toString());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        System.out.println(TAG + " userEventTriggered：" + evt.toString());
        if (evt instanceof IdleStateEvent) {
            System.out.println(TAG + " userEventTriggered state：" + ((IdleStateEvent) evt).state());
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                sendHeartPkg(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    /**
     * 发送心跳
     */
    private void sendHeartPkg(ChannelHandlerContext ctx) {
        PkgDataBean bean = new PkgDataBean();
        bean.setCmd((byte) 0x02);
        bean.setData("心跳数据包");
        bean.setDataLength((byte) bean.getData().getBytes().length);
        ctx.channel().writeAndFlush(bean);
        System.out.println(TAG + "客户端发送心跳成功");
    }

    /**
     * 5s重连一次服务端
     */
    private void reConnect(final ChannelHandlerContext ctx) {
        EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(TAG +  "连接断开，发起重连");
                client.reConnect();
            }
        }, 5, TimeUnit.SECONDS);
    }

}
