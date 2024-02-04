package cn.floatingpoint.min.system.hyt.party;

import cn.floatingpoint.min.system.ui.hyt.party.VexViewButton;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class VexViewDecoder {
    private final String[] elements;
    public final boolean sign;
    public String result;

    public VexViewDecoder(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        result = decode(bytes);
        if (!result.contains("[but]手动输入")) {
            sign = result.contains("[gui]https://img.166.net/gameyw-misc/opd/squash/20221221/104939-4q3d0pgm59.png");
            if (sign) {
                result = result.replace("[but]", "[but]sign");
            }
        } else {
            sign = false;
        }
        elements = result.split("<&>");
    }

    private String decode(byte[] bytes) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] array = new byte[256];
            int read;
            while ((read = gZIPInputStream.read(array)) >= 0) {
                byteArrayOutputStream.write(array, 0, read);
            }
            return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
        return "";
    }

    public VexViewButton getButton(String name) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (e.endsWith("[but]" + name)) {
                return new VexViewButton(name, this.elements[i + 6]);
            }
        }
        return null;
    }

    public String getElement(int index) {
        return elements[index];
    }
}
