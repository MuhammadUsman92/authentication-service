package com.muhammadusman92.authenticationservice.controller;

import com.muhammadusman92.authenticationservice.payloads.Response;
import com.muhammadusman92.authenticationservice.payloads.UserDto;
import com.muhammadusman92.authenticationservice.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import org.springframework.util.MimeTypeUtils;
import java.net.URLConnection;
import java.net.URLEncoder;

import static java.time.LocalDateTime.now;
import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @PostMapping("/upload/")
    public ResponseEntity<Response> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = this.fileService.uploadFile(path, file);
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .message("File uploaded successfully")
                .data(fileName)
                .build(), OK);
    }



    @GetMapping(value = "/{fileName}")
    public void downloadFile(
            @PathVariable String fileName,
            HttpServletResponse response
    ) throws IOException {
        InputStream resource = this.fileService.getResource(path, fileName);

        // Determine the file's content type based on the file extension
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }

        // Set the content type header
        response.setContentType(contentType);

        // Set the content disposition header with the filename
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

        StreamUtils.copy(resource, response.getOutputStream());
    }


}
