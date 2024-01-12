package cn.floatingpoint.min.utils.math;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class DHUtil {
    private static final String SELECT_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 512;
    private static final String PUBLIC_KEY = "DHPublicKey";
    private static final String PRIVATE_KEY = "DHPrivateKey";

    public static Map<String, Object> initKey() {
        try {
            //实例化密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            //初始化密钥对生成器
            keyPairGenerator.initialize(KEY_SIZE);
            //生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //甲方公钥
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
            //甲方私钥
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
            //将密钥对存储在Map中
            Map<String, Object> keyMap = new HashMap<>(2);
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKeySpec getSecretKey(final byte[] key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(SELECT_ALGORITHM);
            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(key));
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), SELECT_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));// 初始化为加密模式的密码器
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            //执行操作
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /** 构建密钥
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return byte[] 本地密钥
     */
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            //初始化私钥
            //密钥材料转换
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            //产生私钥
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //实例化
            KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
            //初始化
            keyAgree.init(priKey);
            keyAgree.doPhase(pubKey, true);
            //生成本地密钥
            SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);
            return secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 取得私钥
     * @param keyMap 密钥Map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /** 取得公钥
     * @param keyMap 密钥Map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }
}