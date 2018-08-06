package com.accounted4.am.common;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;

/**
 * Wrap REST responses in a standard structure.
 * 
 * @author gheinze
 * @param <T>
 */
@Data
public class RestResponse<T> {

    private final T data;
    private final Meta meta;
    private final Status status;


    public RestResponse(T data) {
        this(data, new Status());
    }


    public RestResponse(T data, Status status) {
        this.data = data;
        this.status = status;
        this.meta = new Meta();
    }


    @Getter
    public static class Meta {

        private final LocalDateTime localDateTime;

        public Meta() {
            this.localDateTime = LocalDateTime.now();
        }

    }


    @Data
    public static class Status {

        private final String msg;
        private final String description;

        public Status() {
            this("ok", "ok");
        }

        public Status(String msg, String description) {
            this.msg = msg;
            this.description = description;
        }

    }


}
