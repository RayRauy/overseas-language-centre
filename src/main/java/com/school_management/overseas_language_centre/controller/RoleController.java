package com.school_management.overseas_language_centre.controller;

import com.school_management.overseas_language_centre.base.BaseApi;
import com.school_management.overseas_language_centre.base.BaseApiPagination;
import com.school_management.overseas_language_centre.dto.apiresponses.SuccessResponse;
import com.school_management.overseas_language_centre.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.dto.pagination.PageDTO;
import com.school_management.overseas_language_centre.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;

    RoleController (RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApi<RoleResponse>> getById(@PathVariable Long id){

        RoleResponse role = roleService.getById(id);

        return ResponseEntity.ok(
                BaseApi.<RoleResponse>builder()
                        .status(true)
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .timestamp(LocalDateTime.now())
                        .data(role)
                        .build()
                );
    }

    @GetMapping("/getAll")
    public ResponseEntity<SuccessResponse<List<RoleResponse>>> getAll(@ModelAttribute RoleFilter name){

        List<RoleResponse> role = roleService.getAll(name);

        return ResponseEntity.ok(
                SuccessResponse.success(
                        "Successfully Retrieved Roles",
                        role
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<RoleResponse>> create(@Valid @RequestBody RoleRequest request){

        RoleResponse role = roleService.create(request);

        return ResponseEntity.ok(SuccessResponse.success("Role Created Successfully", role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<RoleResponse>> updateById(@Valid @PathVariable Long id, @RequestBody RoleRequest request){

        RoleResponse role = roleService.updateById(id, request);

        return ResponseEntity.ok(SuccessResponse.success("Roles Updated Successfully", role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<RoleResponse>> deleteById(@PathVariable Long id){

        roleService.deleteById(id);

        return ResponseEntity.ok(SuccessResponse.success("Roles Deleted Successfully", null));
    }

    @GetMapping("/pagination")
    public ResponseEntity<?> getAllPagination(RoleFilter filter){
        Page<RoleResponse> allPagination = roleService.getAllPagination(filter);
        PageDTO pageDTO = new PageDTO(allPagination);
        return ResponseEntity.ok(
                BaseApiPagination.<RoleResponse>builder()
                        .status(true)
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .timestamp(LocalDateTime.now())
                        .pagination(pageDTO.getPagination())
                        .data((List<RoleResponse>) pageDTO.getItems())
                        .build()
        );
    }
}
