package com.mychat_sys.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("mails")
public class Mail implements Serializable {
    @Id
    private String id;
    @Indexed
    private String writerId;
    @Indexed
    private String readerId;

    private String content;

    private String dateStr;

    private String month;

    public Mail(String id, String writerId, String readerId, String content, String dateStr, String month) {
        this.id = id;
        this.writerId = writerId;
        this.readerId = readerId;
        this.content = content;
        this.dateStr = dateStr;
        this.month = month;
    }

    public Mail(){

    }

    @Override
    public String toString() {
        return "Mail{" +
                "id='" + id + '\'' +
                ", writerId='" + writerId + '\'' +
                ", readerId='" + readerId + '\'' +
                ", content='" + content + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", month='" + month + '\'' +
                '}';
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
