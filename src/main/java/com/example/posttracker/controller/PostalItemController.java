package com.example.posttracker.controller;

import com.example.posttracker.model.dto.AppError;
import com.example.posttracker.model.dto.CreatePostalDto;
import com.example.posttracker.model.dto.PostalItemDto;
import com.example.posttracker.model.dto.PostalItemFullDto;
import com.example.posttracker.service.PostalItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/postalItem")
@RequiredArgsConstructor
public class PostalItemController {
    private final PostalItemService postalItemService;


    @GetMapping("")
    public ResponseEntity<?> getPostalItems() {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_GATEWAY.value(),
                "Команда \"/postalItem\" без указания трек номера не поддерживается. " +
                        "Вы можете посмотреть все посылки, находящиеся в ПО, в котором вы числитесь, как сотрудник." +
                        " Для этого авторизуйтесь и вызовите команду \"/postOffice/postal\""), HttpStatus.BAD_GATEWAY);
    }

    @PatchMapping("/{tracker}")
    public ResponseEntity<?> patchPostalItems(@PathVariable(required = false) String tracker) {

        return new ResponseEntity<>(new AppError(HttpStatus.BAD_GATEWAY.value(),
                "Команда Patch \"/postalItem\" не поддерживается. Вы не можете изменять созданные отправления." +
                        " Если вы допустили ошибку, создайте новое правильное отправление." +
                        " Запишите трек номер ошибочного отправления и обратитесь к администратору вашего ПО для его удаления. ")
                , HttpStatus.BAD_GATEWAY);
    }


    @Operation(
            operationId = "postPostalItem",
            tags = {"Сохранить информацию об отправлении"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*", schema = @Schema(implementation = PostalItemFullDto.class))
                    }),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostalItemFullDto> addPostalItem(@RequestBody @Valid CreatePostalDto postalDto,
                                                           Authentication authentication) {
        PostalItemFullDto itemDto = postalItemService.addPostalItem(postalDto, authentication.getName());
        return ResponseEntity.ok(itemDto);
    }

    @Operation(
            operationId = "getPostalItem",
            tags = {"Получить информацию об отправлении по трек номеру"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*", schema = @Schema(implementation = PostalItemDto.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @GetMapping("/{tracker}")
    public ResponseEntity<?> getPostalItem(@PathVariable String tracker,
                                           Authentication authentication) {
        if (authentication != null && !authentication.getAuthorities().isEmpty()) {
            PostalItemFullDto itemDto = postalItemService.getAdminItem(tracker);
            return ResponseEntity.ok(itemDto);
        }
        PostalItemDto itemDto = postalItemService.getUserItem(tracker);
        return ResponseEntity.ok(itemDto);
    }

    @Operation(
            operationId = "DeletePostalItem",
            tags = {"Удалить информацию об отправлении по трек номеру"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "*/*", schema = @Schema(implementation = PostalItemFullDto.class))
                    }),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @DeleteMapping("/{tracker}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity deletePostalItem(@PathVariable String tracker, Authentication authentication) {
        postalItemService.deleteItem(tracker, authentication);
        return ResponseEntity.ok().build();
    }


}



