package com.dream.six.service.impl;


import com.dream.six.entity.RolePermissionEntity;
import com.dream.six.vo.response.RoleResponseVO;
import com.dream.six.enums.RoleEnum;
import com.dream.six.repository.RolePermissionRepository;
import com.dream.six.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserPermissionServiceImpl {

  private final RoleService roleService;
  private final RolePermissionRepository rolePermissionRepository;

  @Autowired
  public UserPermissionServiceImpl(
          RoleService roleService, RolePermissionRepository rolePermissionRepository) {
    this.roleService = roleService;
    this.rolePermissionRepository = rolePermissionRepository;
  }

  public boolean hasPermission(Authentication authentication, String requestedPath) {
    if (authentication != null) {

      Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
      // Extract authorities (roles) as a list of strings
      List<String> roles =authorities.stream().map(GrantedAuthority::getAuthority) // Get authority (role) for each GrantedAuthority
              .toList(); // Collect into a list

      if (roles.contains(RoleEnum.ROLE_SUPER_ADMIN.toString())) {
        return true;
      }

      List<UUID> roleResponseUUIDs = roleService.findByListOfNamesAndIsDeletedFalse(roles).stream()
              .map(RoleResponseVO::getId) // Map RoleResponseVO to UUID
              .toList(); // Collect UUIDs into a List

      Set<RolePermissionEntity> rolePermissionEntitiesSet =
              rolePermissionRepository.findAllByRoleIdsIsRevokedFalse(roleResponseUUIDs).stream().collect(Collectors.toSet());

      List<String> patterns =rolePermissionEntitiesSet.stream()
              .map(entity -> entity.getPermission().getEndPointName())
              .toList();

      return pathMatches(patterns, requestedPath);
    }
    return false;
   }

  private boolean pathMatches(List<String> patterns, String path) {
    // Strip query parameters from the path
    String cleanPath = stripQueryParameters(path);
    // Convert patterns to regex once
    List<Pattern> regexPatterns =
        patterns.stream().map(this::convertPatternToRegex).map(Pattern::compile).toList();

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
