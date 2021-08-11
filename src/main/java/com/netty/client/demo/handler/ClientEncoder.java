package com.netty.client.demo.handler;

import com.netty.client.demo.model.PkgDataBean;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器-对发送的数据编码
 */
public class ClientEncoder  extends MessageToByteEncoder<PkgDataBean> {
    private static final String TAG = "ClientEncoder";
    @Override
    protected void encode(ChannelHandlerContext ctx, PkgDataBean data, ByteBuf out) throws Exception {
        System.out.println(TAG + "encode:" + data);
//        out.writeBytes(msg.getData().getBytes());
        //根据数据包协议，生成byte数组
        byte[] bytes = {0x2A, data.getCmd(), data.getDataLength()};
        byte[] dataBytes = data.getData().getBytes();
        //将所有数据合并成一个byte数组
        byte[] all = byteMergerAll(bytes, dataBytes,new byte[]{0x2A});
        System.out.println(TAG + "encode all.length:" + new String(all));
        //发送数据
        out.writeBytes(all);
    }

    private static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }
}
