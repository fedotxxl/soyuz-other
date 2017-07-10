package io.belov.soyuz.recaptcha;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.belov.soyuz.jersey.client.JerseyClientUtils;
import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 17.12.16.
 */
public class RecaptchaValidator {

    private static final LoggerEvents loge = LoggerEvents.getInstance(RecaptchaValidator.class);

    private Config config;
    private WebTarget target;

    public RecaptchaValidator(Config config) {
        this.config = config;
        this.target = JerseyClientUtils
                .getClientWithJacksonSupport(config.getConnectionTimeoutInSeconds() * 1000, config.getReadTimeoutInSeconds() * 1000)
                .target(config.getValidationUrl());
    }

    public Result validate(String userResponse, String userIp) {
        loge.debug("recaptcha.validate", to.map("config", config, "userResponse", userResponse, "ip", userIp));

        Result result = target
                .queryParam("secret", config.getSecretKey())
                .queryParam("response", userResponse)
                .queryParam("remoteip", userIp)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.text("abc"))
                .readEntity(Result.class);

        return result;
    }


    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private String validationUrl;
        private String secretKey;
        private int connectionTimeoutInSeconds;
        private int readTimeoutInSeconds;
    }

    @Getter
    public static class Result {
        private boolean success;
        private List<ErrorCode> errorCodes = new ArrayList<>();

        @JsonCreator
        public Result(
                @JsonProperty("success") boolean success,
                @JsonProperty("error-codes") List<ErrorCode> errorCodes
        ) {
            this.success = success;
            this.errorCodes = errorCodes == null ? new ArrayList<ErrorCode>() : errorCodes;
        }

        @JsonIgnore
        public boolean isFailure() {
            return !success;
        }

        public boolean hasError(ErrorCode error) {
            return errorCodes.contains(error);
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "success=" + success +
                    ", errorCodes=" + errorCodes +
                    '}';
        }

        public enum ErrorCode {

            //reCAPTCHA verification errors
            MISSING_SECRET_KEY("missing-input-secret"),
            INVALID_SECRET_KEY("invalid-input-secret"),
            MISSING_USER_CAPTCHA_RESPONSE("missing-input-response"),
            INVALID_USER_CAPTCHA_RESPONSE("invalid-input-response"),

            //Custom errors
            MISSING_USERNAME_REQUEST_PARAMETER("missing-username-request-parameter"),
            MISSING_CAPTCHA_RESPONSE_PARAMETER("missing-captcha-response-parameter"),
            VALIDATION_HTTP_ERROR("validation-http-error");

            private final String text;

            ErrorCode(String text) {
                this.text = text;
            }

            @JsonCreator
            private static ErrorCode fromValue(String value) {
                if (value == null) {
                    return null;
                }
                switch (value) {
                    case "missing-input-secret":
                        return MISSING_SECRET_KEY;
                    case "invalid-input-secret":
                        return INVALID_SECRET_KEY;
                    case "missing-input-response":
                        return MISSING_USER_CAPTCHA_RESPONSE;
                    case "invalid-input-response":
                        return INVALID_USER_CAPTCHA_RESPONSE;
                    case "missing-username-request-parameter":
                        return MISSING_USERNAME_REQUEST_PARAMETER;
                    case "missing-captcha-response-parameter":
                        return MISSING_CAPTCHA_RESPONSE_PARAMETER;
                    default:
                        throw new IllegalArgumentException("Invalid error code");
                }
            }

            @JsonValue
            public String getText() {
                return text;
            }
        }
    }
}
