package com.tasktracker.app.utils;

public class VerifyData {

  /**
   * Verify if a int is > 0. If is less than throw IllegalArgumentException.
   *
   * @param i int
   * @param m String
   */
  public static void verifyInt(int i, String m) {
    if (i < 0) {
      throw new IllegalArgumentException(m);
    }
  }

  /**
   * Verify if the string have a value, if doesnt have one throw IllegalArgumentException.
   *
   * @param i String
   * @param m String (message of the error)
   */
  public static void verifyString(String i, String m) {
    if (i == null || i.isBlank()) {
      throw new IllegalArgumentException(m);
    }
  }

  /**
   * Verify if the value is in the Enumeration, if is not there throw IllegalArgumentException using
   * the m ass message.
   *
   * @param data The enum to veryfy if the value is in there
   * @param value String
   * @param m Message of error
   */
  public static <E extends Enum<E>> void verifyEnum(String value, Class<E> data, String m) {
    try {
      Enum.valueOf(data, value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(m);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(m);
    }
  }

  /**
   * Verify if the data have value or is null, if is null or empty return null if not return the
   * data.
   *
   * @param data String
   * @return String
   */
  public static String verifyStringForCli(String data) {
    return data == null || data.isBlank() ? null : data;
  }
}
