package sk.stuba.fei.uim.vsa.pr2.model.dto.response;

import lombok.Data;

@Data
public class MessageResponse {

    private Integer code;
    private String message;
    private Error error;

    public MessageResponse() {
    }

    @Data
    public static class Error {
        private String type;
        private String trace;

        public Error() {
        }
    }
}
