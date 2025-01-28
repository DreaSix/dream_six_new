package com.dream.six.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PathMatcher {
  // For testing the paths
  public static void main(String[] args) {
    PathMatcher matcher = new PathMatcher();

    List<String> patterns = List.of("/users/{userId}", "/orders/*", "/products/*/details");
    String path1 = "/users/123";
    String path2 = "/orders/456";
    String path3 = "/products/789/details?raj=123,45&data=456";
    String path4 = "/products/789";

    System.out.println(matcher.pathMatches(patterns, path1)); // true
    System.out.println(matcher.pathMatches(patterns, path2)); // true
    System.out.println(matcher.pathMatches(patterns, path3)); // true
    System.out.println(matcher.pathMatches(patterns, path4)); // false
  }

  public boolean pathMatches(List<String> patterns, String path) {
    // Strip query parameters from the path
    String cleanPath = stripQueryParameters(path);

    // Convert patterns to regex once
    List<Pattern> regexPatterns =
        patterns.stream()
            .map(this::convertPatternToRegex)
            .map(Pattern::compile)
            .collect(Collectors.toList());

    // Check if any pattern matches the cleaned path
    return regexPatterns.stream()
        .map(pattern -> pattern.matcher(cleanPath))
        .anyMatch(Matcher::matches);
  }

  private String stripQueryParameters(String path) {
    int queryIndex = path.indexOf('?');
    if (queryIndex != -1) {
      return path.substring(0, queryIndex);
    }
    return path;
  }

  private String convertPatternToRegex(String pattern) {
    // Escape special regex characters except '*'
    String escapedPattern = pattern.replaceAll("([\\.\\^\\$\\+\\?\\|\\(\\)\\[\\]\\\\])", "\\\\$1");

    // Replace '*' with regex to match any character except '/'
    if (escapedPattern.contains("*")) {
      escapedPattern = escapedPattern.replace("*", "[^/]*");
    }

    // Convert path variables {variable} to regex [^/]+
    escapedPattern = escapedPattern.replaceAll("\\{[^/]+}", "[^/]+");

    return escapedPattern;
  }
}
