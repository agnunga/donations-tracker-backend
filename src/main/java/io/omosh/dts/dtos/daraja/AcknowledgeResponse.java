package io.omosh.dts.dtos.daraja;

import lombok.Data;

@Data
public class AcknowledgeResponse {

    public AcknowledgeResponse(String message){
        this.message = message;
    }
    private String message;
}
