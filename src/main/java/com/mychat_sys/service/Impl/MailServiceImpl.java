package com.mychat_sys.service.Impl;

import com.mychat_sys.bean.Mail;
import com.mychat_sys.dao.MailDao;
import com.mychat_sys.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    MailDao mailDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Object> getMailByMonth(String month, String userId) {
        String key = "mail_" + userId + month +":list";
        boolean hasKey = redisTemplate.hasKey(key);
        ListOperations listOperations = redisTemplate.opsForList();
        List<Object> mails;
        if(hasKey){
            mails = listOperations.range(key, 0, -1);
            log.info("缓存取"+mails);
        }else{
            mails = mailDao.findMailByMonthAndUserId(month,userId);
            listOperations.rightPushAll(key,mails);
            redisTemplate.expire(key,1,TimeUnit.HOURS);
            log.info("缓存放"+mails);
        }
        return mails;
    }

    @Override
    public Mail getMailById(String id) {
        String key = "mail_"+id;
        boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations<String, Mail> operations = redisTemplate.opsForValue();
        if(hasKey){
//            System.out.println("缓存取");
            Mail result = operations.get(key);
            log.info("缓存取数据mail数据:"+result.getDateStr());
            return result;
        }else{
            Mail mail = mailDao.findMailById(id);
            log.info("缓存放数据mail数据:"+mail.getDateStr());
            operations.set(key,mail,1, TimeUnit.HOURS);
            return mail;
        }
    }

    @Override
    public long updateMailById(String content,String id) {
        long result = mailDao.updateMailById(content,id);
        String key = "mail_"+id;
        ValueOperations<String, Mail> operations = redisTemplate.opsForValue();
//        System.out.println(result);
        if(result==1l){
            Mail mail = mailDao.findMailById(id);
            log.info("缓存放数据mail数据:"+mail.getDateStr());
            operations.set(key,mail,1, TimeUnit.HOURS);
        };
        return result;
    }

    @Override
    public Mail saveMail(Mail mail) {
        Mail saveRes = mailDao.saveMail(mail);
        String key = "mail_"+saveRes.getId();
        String mails_key_writer = "mail_" + mail.getWriterId() + mail.getMonth() +":list";
        String mails_key_reader = "mail_" + mail.getReaderId() + mail.getMonth() +":list";
        ValueOperations<String, Mail> operations = redisTemplate.opsForValue();
        ListOperations listOperations = redisTemplate.opsForList();
        log.info("缓存放数据mail数据:"+saveRes.getDateStr());
        if(redisTemplate.hasKey(mails_key_writer)){
            HashMap<String,String> mailRecord = new LinkedHashMap();
            mailRecord.put("_id",mail.getId());
            mailRecord.put("dateStr",mail.getDateStr());
            listOperations.rightPush(mails_key_writer,mailRecord);
        }
        if(redisTemplate.hasKey(mails_key_reader)){
            HashMap<String,String> mailRecord = new LinkedHashMap();
            mailRecord.put("_id",mail.getId());
            mailRecord.put("dateStr",mail.getDateStr());
            listOperations.rightPush(mails_key_reader,mailRecord);
        }
        operations.set(key,saveRes,1,TimeUnit.HOURS);
        return saveRes;
    }

    @Override
    public void removeMails(String UserId) {
        mailDao.removeMails(UserId);
    }
}
