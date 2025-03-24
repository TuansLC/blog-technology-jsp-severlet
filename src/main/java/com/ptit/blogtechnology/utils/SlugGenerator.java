package com.ptit.blogtechnology.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {
  private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
  private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

  public static String toSlug(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    slug = EDGESDHASHES.matcher(slug).replaceAll("");

    // Chuyển đổi các ký tự tiếng Việt
    slug = slug.toLowerCase(Locale.ENGLISH)
        .replaceAll("đ", "d")
        .replaceAll("Đ", "d");

    // Xử lý trường hợp đặc biệt
    slug = slug.replaceAll("[-]+", "-"); // Loại bỏ nhiều dấu gạch ngang liên tiếp

    return slug;
  }

  /**
   * Tạo slug từ tiêu đề và thêm hậu tố nếu cần
   * @param title Tiêu đề cần tạo slug
   * @param suffix Hậu tố (có thể là null)
   * @return Slug đã được tạo
   */
  public static String toSlug(String title, String suffix) {
    String slug = toSlug(title);

    if (suffix != null && !suffix.isEmpty()) {
      slug = slug + "-" + suffix;
    }

    return slug;
  }

  /**
   * Tạo slug từ tiêu đề và thêm timestamp để đảm bảo tính duy nhất
   * @param title Tiêu đề cần tạo slug
   * @return Slug đã được tạo với timestamp
   */
  public static String toUniqueSlug(String title) {
    return toSlug(title, String.valueOf(System.currentTimeMillis()));
  }

  /**
   * Kiểm tra xem một chuỗi có phải là slug hợp lệ không
   * @param slug Chuỗi cần kiểm tra
   * @return true nếu là slug hợp lệ, false nếu không
   */
  public static boolean isValidSlug(String slug) {
    if (slug == null || slug.isEmpty()) {
      return false;
    }

    // Slug chỉ chứa chữ cái, số và dấu gạch ngang
    return slug.matches("^[a-z0-9-]+$");
  }
}