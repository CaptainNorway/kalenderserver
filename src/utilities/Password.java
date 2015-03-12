package utilities;

import java.security.MessageDigest;

public class Password {

    public static String hashPassword(String password, String salt) {
        try {
            String passwordToEncrypt = "" + password + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordToEncrypt.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i =0; i<bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println("hashed password: " + sb.toString());
            return sb.toString();
        }
        catch(Exception e) {
            System.out.println("hashPassword failed.");
            e.printStackTrace();
        }
        return null;
    }
}
