package com.netty.client.demo.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.nio.charset.StandardCharsets;

public class MyDelimiterFrameDecoder extends DelimiterBasedFrameDecoder {

    public static final ByteBuf delimiter = Unpooled.copiedBuffer("$".getBytes(StandardCharsets.UTF_8));

    public MyDelimiterFrameDecoder(int maxFrameLength) {
        super(maxFrameLength, delimiter);
    }

    public MyDelimiterFrameDecoder(int maxFrameLength, boolean stripDelimiter) {
        super(maxFrameLength, stripDelimiter, delimiter);
    }

    public MyDelimiterFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast) {
        super(maxFrameLength, stripDelimiter, failFast, delimiter);
    }

}
