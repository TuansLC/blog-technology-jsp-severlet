package com.ptit.blogtechnology.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
  public static String hashPassword(String password) {
    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);

      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] hashedPassword = md.digest(password.getBytes());

      byte[] combined = new byte[salt.length + hashedPassword.length];
      System.arraycopy(salt, 0, combined, 0, salt.length);
      System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

      return Base64.getEncoder().encodeToString(combined);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean checkPassword(String password, String storedHash) {
    try {
      byte[] combined = Base64.getDecoder().decode(storedHash);

      byte[] salt = new byte[16];
      byte[] storedHashedPassword = new byte[combined.length - 16];
      System.arraycopy(combined, 0, salt, 0, 16);
      System.arraycopy(combined, 16, storedHashedPassword, 0, storedHashedPassword.length);

      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] hashedPassword = md.digest(password.getBytes());

      if (hashedPassword.length != storedHashedPassword.length) {
        return false;
      }

      for (int i = 0; i < hashedPassword.length; i++) {
        if (hashedPassword[i] != storedHashedPassword[i]) {
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
