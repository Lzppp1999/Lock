package com.mychat_sys.dao;

import com.mongodb.client.result.UpdateResult;
import com.mychat_sys.bean.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Mail saveMail(Mail mail){
       return mongoTemplate.save(mail,"mails");
    }

    public List<Object> findMailByMonthAndUserId(String Month,String userId){
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria.where("writerId").is(userId),criteria.where("readerId").is(userId));
        Criteria criteriaMonth = new Criteria();
        criteria.andOperator(criteria,criteriaMonth.where("month").is(Month));
        Query query = new Query(criteria);
        query.fields().include("id").include("dateStr");
        query.with(Sort.by(Sort.Order.asc("dateStr")));
        List<Object> mailSnapList = mongoTemplate.find(query, Object.class, "mails");
        return mailSnapList;
    }

    public Mail findMailById(String id){
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query,Mail.class,"mails");
    }

    public long updateMailById(String content,String id){
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("content",content);
        UpdateResult result = mongoTemplate.updateFirst(query,update,Mail.class);
        return result.getModifiedCount();
    }

    public void removeMails(String userId){
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria.where("writerId").is(userId),criteria.where("readerId").is(userId));
        Query query = new Query(criteria);
        mongoTemplate.remove(query,Mail.class,"mails");
    }
}
