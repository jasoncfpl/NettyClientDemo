package com.netty.client.demo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class ClientDecoder extends ByteToMessageDecoder {
    private static final String TAG = "ClientDecoder";
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //收到的数据长度
        int length = in.readableBytes();
        //创建 ByteBuf存储接收到的数据 --TODO Unpooled是什么意思
        ByteBuf byteBuf = Unpooled.buffer(length);
        in.readBytes(byteBuf);

        String data = Arrays.toString(byteBuf.array());
        String data1 = new String(byteBuf.array());
        System.out.println(TAG + "--data:" + data);
        System.out.println(TAG + "--data1:" + data1);

        out.add(data);
    }
}
