package com.technogise.upgrad.backend.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Feedback {
  HELPFUL,
  NOT_HELPFUL;

  public static Set<String> getValidNames() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Checks if the given name is a valid feedback value.
   *
   * @param name the feedback name to validate
   * @return true if valid, false otherwise
   */
  public static boolean isValid(String name) {
    if (name == null) {
      return false;
    }
    try {
      Feedback.valueOf(name);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
