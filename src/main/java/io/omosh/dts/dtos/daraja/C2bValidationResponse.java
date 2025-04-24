package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class C2bValidationResponse {

    @JsonProperty("ResultDesc")
    private String resultDesc;

    @JsonProperty("ResultCode")
    private String resultCode;

    public C2bValidationResponse(String resultDesc, String resultCode) {
        this.resultDesc = resultDesc;
        this.resultCode = resultCode;
    }

//	ResultCode ResultDesc
//	C2B00011 Invalid MSISDN
//	C2B00012 Invalid Account Number
//	C2B00013 Invalid Amount
//	C2B00014 Invalid KYC Details
//	C2B00015 Invalid Shortcode
//	C2B00016 Other Error
//	"ResultDesc": "Rejected"/"ResultDesc": "Accepted",

}
