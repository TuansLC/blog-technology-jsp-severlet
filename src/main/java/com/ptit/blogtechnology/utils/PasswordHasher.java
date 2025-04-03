package com.ptit.blogtechnology.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
  public static String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public static boolean checkPassword(String password, String storedHash) {
    try {
      return BCrypt.checkpw(password, storedHash);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
