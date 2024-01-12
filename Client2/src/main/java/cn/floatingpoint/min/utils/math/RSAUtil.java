package cn.floatingpoint.min.utils.math;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.*;

public class RSAUtil {
    public static byte[] encrypt(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //分段加密
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            //对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 244) {
                    cache = cipher.doFinal(data, offSet, 244);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 244;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //分段解密
            byte[] enBytes = null;
            for (int i = 0; i < data.length; i += 256){
                //注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + 256));
                enBytes = ArrayUtils.addAll(enBytes, doFinal);
            }
            return enBytes;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException | IllegalBlockSizeException
                 | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Key> generateKeys() {
        // 生成公钥和私钥对
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            HashMap<String, Key> map = new HashMap<>();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            map.put("PUBLIC_KEY", publicKey);
            map.put("PRIVATE_KEY", privateKey);
            return map;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}