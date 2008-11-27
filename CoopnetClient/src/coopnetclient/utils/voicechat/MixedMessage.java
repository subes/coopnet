package coopnetclient.utils.voicechat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MixedMessage {

    public static final byte AUDIO_DATA_PACKAGE = 1;
    public static final byte STRING_COMMAND = 2;
    public static final String CHARSET = "UTF-8";
    private static Charset charset = Charset.forName(CHARSET);
    private static CharsetDecoder decoder = charset.newDecoder();
    private static CharsetEncoder encoder = charset.newEncoder();
    public final byte commandType;
    public final String commandString;
    public final byte[] byteData;
    public boolean valid;
    private static Deflater compressor = new Deflater();
    private static Inflater decompressor = new Inflater();
    

    static {
        compressor.setLevel(Deflater.BEST_COMPRESSION);
    }

    /**
    build message from raw data after recieving
     */
    public MixedMessage(ByteBuffer buildFrom) {
        commandType = buildFrom.get(0);
        byte[] tail = new byte[buildFrom.limit() - 1];
        System.arraycopy(buildFrom.array(), 1, tail, 0, tail.length);
        switch (commandType) {

            case AUDIO_DATA_PACKAGE:
                int i = 0;
                for (; i < tail.length; i++) {
                    if (tail[i] == 0) {
                        break;
                    }
                }
                //i is the index after the end of string-data part
                //decode string part
                ByteBuffer buffer = ByteBuffer.allocate(i);
                buffer.put(tail, 0, i);
                buffer.flip();
                String readedString = null;
                byte[] deCompressedData;
                try {
                    CharBuffer charBuffer = decoder.decode(buffer);
                    readedString = charBuffer.toString();
                    byte[] compressedData = new byte[tail.length - (i + 1)];
                    System.arraycopy(tail, i + 1, compressedData, 0, compressedData.length);
                    deCompressedData = deCompressBytes(compressedData);
                } catch (Exception e) {
                    System.out.println("Bad package:" + e.getMessage());
                    valid = false;
                    commandString = null;
                    byteData = null;
                    break;
                }
                commandString = readedString;
                byteData = deCompressedData;
                valid = true;
                break;

            case STRING_COMMAND:
                buffer = ByteBuffer.allocate(tail.length);
                buffer.put(tail);
                buffer.flip();
                readedString = null;
                try {
                    CharBuffer charBuffer = decoder.decode(buffer);
                    readedString = charBuffer.toString();
                } catch (Exception e) {
                    System.out.println("Bad package:" + e.getMessage());
                    valid = false;
                    commandString = null;
                    byteData = null;
                    break;
                }
                commandString = readedString;
                byteData = null;
                valid = true;
                break;

            default:
                commandString = null;
                byteData = null;
                valid = false;
                break;
        }
    }

    /**
    create a String message to send
     */
    public MixedMessage(String commandString) {
        commandType = STRING_COMMAND;
        this.commandString = commandString;
        byteData = null;
        valid = true;
    }

    /**
    create a vioce-data message to send
     */
    public MixedMessage(byte[] data, String username) {
        commandType = AUDIO_DATA_PACKAGE;
        byteData = data;
        commandString = username;
        valid = true;
    }

    /**
    get the bytes to send from message
     */
    public ByteBuffer getBytesToSend() throws CharacterCodingException {
        if (commandType == AUDIO_DATA_PACKAGE) {
            CharBuffer charBuffer = CharBuffer.wrap(commandString);
            ByteBuffer encodedStringData = encoder.encode(charBuffer);
            //compress
            byte[] compressedData = new byte[]{};
            try {
                compressedData = compressBytes(byteData);
            } catch (Exception e) {
                System.out.println("can't make package:" + e.getMessage());
                return null;
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(compressedData.length + encodedStringData.limit() + 7);
            byteBuffer.put(commandType);
            byteBuffer.put(encodedStringData);
            byteBuffer.put(new byte[]{0});
            byteBuffer.put(compressedData);
            byteBuffer.put(new byte[]{-128, 127, -128, 127, -128}); //message delimiter lol
            byteBuffer.flip();
            return byteBuffer;
        } else {
            CharBuffer charBuffer = CharBuffer.wrap(commandString);
            ByteBuffer encodedData = encoder.encode(charBuffer);
            ByteBuffer byteBuffer = ByteBuffer.allocate(encodedData.limit() + 6);
            byteBuffer.put(commandType);
            byteBuffer.put(encodedData);
            byteBuffer.put(new byte[]{-128, 127, -128, 127, -128}); //message delimiter lol
            byteBuffer.flip();
            return byteBuffer;
        }
    }

    private static byte[] compressBytes(byte[] dataToCompress) {
        // Give the compressor the data to compress
        compressor.reset();
        compressor.setInput(dataToCompress);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(dataToCompress.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        return compressedData;
    }

    private static byte[] deCompressBytes(byte[] compressedData) throws DataFormatException {
        decompressor.reset();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            int count = decompressor.inflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();
        return decompressedData;
    }
}
