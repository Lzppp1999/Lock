package com.mychat_sys.dao;

import com.mychat_sys.bean.Mail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MailRepository extends MongoRepository<Mail,String> {

}
