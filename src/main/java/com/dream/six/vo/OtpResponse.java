package com.dream.six.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class OtpResponse {
    private boolean success;
    private String message;

    private String reqId;

    public void extractReqIdFromMessage() {
        if (message != null) {
            try {
                // Convert hex string to normal text
                this.reqId = hexToString(message);
            } catch (Exception e) {
                System.out.println("Error decoding hex: " + e.getMessage());
            }
        }
    }

    private String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}
