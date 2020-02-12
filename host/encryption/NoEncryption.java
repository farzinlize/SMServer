package host.encryption;

import java.util.Arrays;

import host.Coder;

public class NoEncryption implements Coder{

    @Override
    public byte[] code(byte[] data) {
        return Arrays.copyOf(data, data.length);
    }

}