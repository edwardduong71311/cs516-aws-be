package edward.duong.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class EncryptHelper {
    public static String encrypt(String plainText) {
        return BCrypt.withDefaults().hashToString(12, plainText.toCharArray());
    }

    public static boolean verify(String plainText, String hashedPassword) {
        return BCrypt.verifyer().verify(plainText.toCharArray(), hashedPassword.toCharArray()).verified;
    }
}
