package cn.hdfk7.boot.starter.common.redisson;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hdfk7.boot.proto.base.json.JacksonObjectMapperInstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JSONCodec extends BaseCodec {
    public static final JSONCodec INSTANCE = new JSONCodec();
    private final ObjectMapper mapObjectMapper = JacksonObjectMapperInstance.getNonFinalMapper();
    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try {
            ByteBufOutputStream os = new ByteBufOutputStream(out);
            mapObjectMapper.writeValue((OutputStream) os, in);
            return os.buffer();
        } catch (IOException e) {
            out.release();
            throw e;
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
        }
    };
    private final Decoder<Object> decoder = (buf, state) -> mapObjectMapper.readValue((InputStream) new ByteBufInputStream(buf), Object.class);

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

}
