package com.mychat_sys;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.mychat_sys.bean.Mail;
import com.mychat_sys.bean.MyFriends;
import com.mychat_sys.bean.User;
import com.mychat_sys.controller.UserController;
import com.mychat_sys.dao.MailDao;
import com.mychat_sys.idmaker.Sid;
import com.mychat_sys.mapper.MyFriendsMapper;
import com.mychat_sys.service.MailService;
import com.mychat_sys.service.MyLockService;
import com.mychat_sys.service.UserService;
import com.mychat_sys.utils.FastDFSClient;
import com.mychat_sys.utils.FileUtils;
import com.mychat_sys.utils.QRCodeUtils;
import com.mychat_sys.webSocketServer.enums.MsgActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest
@Slf4j
class MychatSysApplicationTests {

    @Autowired
    UserController userController;

    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    MyFriendsMapper myFriendsMapper;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Autowired
    MailDao mailDao;

    @Autowired
    MyLockService myLockService;

    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    @Test
    void contextLoads() throws IOException {
        MyFriends myLock_to = new MyFriends();
        myLock_to.setId(Sid.nextShort());
        myLock_to.setMyUserId("210913DF2A2AFFW0");
        myLock_to.setMyFriendId("210831CHZCDYMHDP");
        //插入Lock记录
        myFriendsMapper.insert(myLock_to);
    }

    @Test
    void testEmoji(){
        String msg = "An awesome string with a few emojis!";
        String alias = emojiConverter.toAlias(msg);
        String html = emojiConverter.toHtml(msg);
        System.out.println(msg);
        System.out.println(alias);
        System.out.println(html);
    }

    @Test
    void testBase64() throws Exception {
        String base64 = "data:audio/x-wav;base64,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";
        String face_path = "D:\\project_local_redirect\\audioBase64.wav";
        //头像base64编码转为文件对象
        FileUtils.base64ToFile(face_path,base64);
        //FileUtils.fileToWav(face_path);
    }

    @Test
    void testDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dateStr = sdf.format(date);
        System.out.println(dateStr);
    }

    @Test
    void testMongoDB(){
//        Mail mail1 = new Mail("123","lzp1999","yyx1998","我爱你","2022-2-1","2022-2");
//        Mail mail2 = new Mail("567","yyx1998","lzp1999","我不爱你","2022-2-2","2022-2");
//        mailDao.saveMail(mail1);
//        mailDao.saveMail(mail2);
        List<Object> mailByMonth = mailService.getMailByMonth("2022-3", "210831CHZCDYMHDP");
        mailByMonth.add(new LinkedHashMap<String,String>());
        System.out.println(mailByMonth);
//        System.out.println(mailByMonth);
    }
    @Test
    void testLog(){
        log.info("这是log4j2的日志测试，info级别");
        log.warn("这是log4j2的日志测试，warn级别");
        log.error("这是log4j2的日志测试，error级别");
    }

    @Test
    void testCache(){
        myLockService.getMyLockInfo("210913DF2A2AFFW0");
        System.out.println(myLockService.getMyLockInfo("210913DF2A2AFFW0").getNickname());
        userService.modifyNickname("想尹宝","210831CHZCDYMHDP");
        System.out.println(myLockService.getMyLockInfo("210913DF2A2AFFW0").getNickname());
    }

    @Test
    void testInteger(){
        System.out.println(MsgActionEnum.UNLCOK.type == 6);
    }
}
