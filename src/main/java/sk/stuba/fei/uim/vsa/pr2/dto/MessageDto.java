package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Integer code;
    private String message;
    private Error error;
}
