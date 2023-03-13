package com.example.posttracker.controller;

import com.example.posttracker.model.dto.PostmanDto;
import com.example.posttracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/postman")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            operationId = "getPostman",
            tags = {"Получить информацию о сотруднике по номеру"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*", schema = @Schema(implementation = PostmanDto.class))
                    }),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostmanDto> getPostman(@PathVariable String id) {
        PostmanDto postmanDto = userService.getPostman(id);
        return ResponseEntity.ok(postmanDto);
    }


    @Operation(
            operationId = "DeletePostman",
            tags = {"Удалить информацию о сотруднике по номеру"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*")
                    }),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity deletePostman(@PathVariable String id, Authentication authentication) {
        userService.deletePostman(id, authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(
            operationId = "PatchPostman",
            tags = {"Изменить информацию о сотруднике по номеру"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*", schema = @Schema(implementation = PostmanDto.class))
                    }),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<PostmanDto> patchPostman(@PathVariable String id, @RequestBody() PostmanDto postmanDto,
                                       Authentication authentication) {
        PostmanDto newPostman=  userService.patchPostman(id, postmanDto, authentication);
        return ResponseEntity.ok(newPostman);
    }

}




