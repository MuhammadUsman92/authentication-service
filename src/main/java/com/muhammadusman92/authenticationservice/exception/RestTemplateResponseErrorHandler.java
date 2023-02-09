package com.muhammadusman92.authenticationservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muhammadusman92.authenticationservice.payloads.UserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler 
  implements ResponseErrorHandler {
//    @Autowired
    private ObjectMapper mapper;


    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
      throws IOException {

        return (
          httpResponse.getStatusCode().series() == CLIENT_ERROR 
          || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
      throws IOException {

        if (httpResponse.getStatusCode()
          .series() == Series.SERVER_ERROR) {
            // handle SERVER_ERROR
            System.out.println(httpResponse.getStatusCode());
        } else if (httpResponse.getStatusCode()
          .series() == Series.CLIENT_ERROR) {
                // handle CLIENT_ERROR
                if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    byte[] body = this.getResponseBody(httpResponse);
                    String errorMessage = this.getErrorMessage(httpResponse.getRawStatusCode(), httpResponse.getStatusText(), body, this.getCharset(httpResponse));
                    UserResponse accountResponse= mapper.readValue(errorMessage,UserResponse.class);
                    throw new AccountServiceException(accountResponse.getMessage());
                }
        }
    }

    private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset charset) {
        String preface= "";
        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        } else {
            charset = charset != null ? charset : StandardCharsets.UTF_8;
            String bodyText = new String(responseBody, charset);
            return preface + bodyText;
        }
    }
    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException var3) {
            return new byte[0];
        }
    }
    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharset() : null;
    }
}