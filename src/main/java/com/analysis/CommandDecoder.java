package com.analysis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class CommandDecoder extends ReplayingDecoder<CommandDecoder.State>{

    public enum State{
        ARGS,
        BYTE_ARGS,
        ARGS_DATA
    }

    private static final char R = '\r';
    private static final char N = '\n';

    public CommandDecoder() {
        state(State.ARGS);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        RedisFrame redisFrame = decoder(channelHandlerContext, byteBuf, list);

        if (redisFrame != null) {
            list.add(redisFrame);
        }
    }

    private RedisFrame decoder(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        RedisFrame redisFrame = null;
        int len = 0;
        int count = 0;
        while (true) {
            switch (state()) {
                case ARGS:
                    if (byteBuf.readByte() != "*") {
                        throw new DecoderException("no *");
                    }
                    count = parseRedisNumber(byteBuf);
                    redisFrame = new RedisFrame(count);
                    checkpoint(State.BYTE_ARGS);
                    break;
                case BYTE_ARGS:
                    if (byteBuf.readByte() != "$") {
                        throw new DecoderException("no $");
                    }
                    len = parseRedisNumber(byteBuf);
                    checkpoint(State.ARGS_DATA);
                    break;
                case ARGS_DATA:
                    redisFrame.appendArgs(byteBuf.readBytes(len).array());
                    if (byteBuf.readByte() != R || byteBuf.readByte() != N){
                        throw new DecoderException("no r or n");
                    }
                    if ((--count) <= 0) {
                        return redisFrame;
                    } else {
                        checkpoint(State.BYTE_ARGS);
                    }
                    break;
                default:
                    throw new DecoderException("default");

            }
        }
    }

    private int parseRedisNumber(ByteBuf byteBuf) {
        byte readByte = byteBuf.readByte();
        boolean t = readByte == '-';
        if (t) {
            readByte = byteBuf.readByte();
        }
        int result = 0;
        do {
            int digit = readByte - '0';
            if (digit >= 0 && digit < 10) {
                result = (result * 10) + digit;
            } else {
                throw new DecoderException("");
            }
        } while ((readByte = byteBuf.readByte()) != R);

        if ((readByte = byteBuf.readByte()) != N) {
            throw new DecoderException("");
        }
        return (t ? -result:result);
    }
}
