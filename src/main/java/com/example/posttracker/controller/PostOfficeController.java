package com.example.posttracker.controller;

import com.example.posttracker.model.dto.*;
import com.example.posttracker.service.PostOfficeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/postOffice")
@RequiredArgsConstructor
public class PostOfficeController {
    private final PostOfficeService postOfficeService;


    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PostOfficeDto>> getAllPostOffice() {
        List<PostOfficeDto> all = postOfficeService.getAllOffice();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{index}")
    public ResponseEntity<PostOfficeDto> getPostOffice(@PathVariable Integer index) {
        PostOfficeDto office = postOfficeService.getOffice(index);
        return ResponseEntity.ok(office);
    }

    @GetMapping("/{index}/postalItem")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PostalItemFullDto>> getAllPostalInPostOffice(@PathVariable Integer index, Authentication authentication) {
        List<PostalItemFullDto> all = postOfficeService.getAllPostalInPostOffice(index, authentication);
        return ResponseEntity.ok(all);
    }


    @PatchMapping("/{index}")
    public ResponseEntity<PostOfficeDto> patchPostOffice(@PathVariable Integer index,
                                                         @RequestBody PostOfficeDto postOfficeDto,
                                                         Authentication authentication) {

        PostOfficeDto office = postOfficeService.updateOffice(index, postOfficeDto, authentication);
        return ResponseEntity.ok(office);
    }


    @PostMapping()
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<PostOfficeDto> addPostOffice(@RequestBody PostOfficeDto postOfficeDto) {
        PostOfficeDto office = postOfficeService.addOffice(postOfficeDto);
        return ResponseEntity.ok(office);
    }

    @PostMapping("/postalItem/{tracker}")

    public ResponseEntity<PostalItemFullDto> addPostalToOffice(@PathVariable String tracker,
                                                               Authentication authentication) {
        PostalItemFullDto item = postOfficeService.addPostalToOffice(tracker, authentication.getName());
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/postalItem/{tracker}")

    public ResponseEntity<PostalItemFullDto> deletePostalToOffice(@PathVariable String tracker,
                                                                  Authentication authentication) {
        PostalItemFullDto item = postOfficeService.deletePostalToOffice(tracker, authentication);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/postalItem/{tracker}/recipient/{phone}")
    public ResponseEntity<PostalItemFullDto> givePostalToRecipient(@PathVariable String tracker,
                                                                   @PathVariable Long phone,
                                                                   Authentication authentication) {
        PostalItemFullDto item = postOfficeService.givePostalToRecipient(tracker, phone, authentication);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{index}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity deleteOffice(@PathVariable int index) {
        postOfficeService.deleteOffice(index);
        return ResponseEntity.ok().build();
    }


}




