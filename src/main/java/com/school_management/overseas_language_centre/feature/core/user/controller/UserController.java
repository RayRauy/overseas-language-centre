package com.school_management.overseas_language_centre.feature.core.user.controller;

import com.school_management.overseas_language_centre.base.BaseApi;
import com.school_management.overseas_language_centre.base.BaseApiPagination;
import com.school_management.overseas_language_centre.dto.pagination.PageDTO;
import com.school_management.overseas_language_centre.feature.core.user.dto.filter.UserFilter;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import com.school_management.overseas_language_centre.feature.core.user.service.UserService;
import com.school_management.overseas_language_centre.feature.core.user.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<BaseApi<UserResponse>> getById(@PathVariable Long id) {
        UserResponse user = userService.getById(id);
        return ResponseEntity.ok(
                BaseApi.success(
                        "User Successfully Retrieved",
                        user
                )
        );
    }

    @GetMapping("All")
    public ResponseEntity<BaseApi<List<UserResponse>>> getAll(@ModelAttribute UserFilter filter) {
        List<UserResponse> user = userService.getAll(filter);
        return ResponseEntity.ok(
                BaseApi.success(
                        "User Successfully Retrieved",
                        user
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseApi<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.ok(
                BaseApi.success(
                        "User Successfully Created",
                        user
                )
        );
    }

    @PutMapping
    public ResponseEntity<BaseApi<UserResponse>> updateById(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse user = userService.updateById(id, request);
        return ResponseEntity.ok(
                BaseApi.success(
                        "User Successfully Updated",
                        user
                )
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseApi<Void>> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(
                BaseApi.success(
                        "User Successfully Deleted",
                        null
                )
        );
    }

    @GetMapping
    public ResponseEntity<BaseApiPagination<List<UserResponse>>> getAuthenticatedUser(UserFilter filter) {
        Page<UserResponse> allPagination = userService.getAllPagination(filter);
        PageDTO<UserResponse> pageDTO = new PageDTO<>(allPagination);
        return ResponseEntity.ok(
                BaseApiPagination.success(
                        "Get pagination Success",
                        pageDTO.getPagination(),
                        pageDTO.getItems()
                )
        );
    }
}
