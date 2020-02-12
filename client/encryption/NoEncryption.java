package client.encryption;

import java.util.Arrays;

import client.Decoder;

public class NoEncryption implements Decoder{

    @Override
    public byte[] decode(byte[] encrepted) {
        return Arrays.copyOf(encrepted, encrepted.length);
    }

}