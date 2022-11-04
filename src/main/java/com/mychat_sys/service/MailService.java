package com.mychat_sys.service;

import com.mychat_sys.bean.Mail;

import java.util.List;

public interface MailService {

    public List<Object> getMailByMonth(String month,String userId);

    public Mail getMailById(String id);

    public long updateMailById(String content,String id);

    public Mail saveMail(Mail mail);

    public void removeMails(String UserId);
}
