package com.technogise.upgrad.backend.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum Interest {
  PYTHON_PROGRAMMING(
      "Python Programming",
      "Learn Python programming fundamentals and advanced concepts",
      "puzzle"),
  DATA_SCIENCE(
      "Data Science", "Master data analysis, visualization, and machine learning", "chart"),
  UI_UX_DESIGN("UI/UX Design", "Create beautiful and user-friendly interfaces", "palette"),
  DIGITAL_MARKETING(
      "Digital Marketing", "Learn digital marketing strategies and analytics", "megaphone"),
  CLOUD_COMPUTING("Cloud Computing", "Explore cloud platforms and distributed systems", "server"),
  CYBERSECURITY("Cybersecurity", "Understand security principles and best practices", "shield"),
  REACT_FRAMEWORK("React Framework", "Build modern web applications with React", "atom"),
  PERSONAL_FINANCE(
      "Personal Finance", "Manage your finances and investments effectively", "dollar");

  private final String displayName;
  private final String description;
  private final String iconName;

  Interest(String displayName, String description, String iconName) {
    this.displayName = displayName;
    this.description = description;
    this.iconName = iconName;
  }

  public static List<Interest> getAll() {
    return Arrays.asList(values());
  }

  public static Set<String> getValidNames() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Checks if the given name is a valid interest.
   *
   * @param name the interest name to validate
   * @return true if valid, false otherwise
   */
  public static boolean isValid(String name) {
    if (name == null) {
      return false;
    }
    try {
      Interest.valueOf(name);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
