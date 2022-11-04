package com.mychat_sys.Vo;


import org.springframework.stereotype.Component;

@Component
public class userBo {

    private String userId;

    private String faceData;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFaceData() {
        return faceData;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }
}
