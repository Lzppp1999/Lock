package com.mychat_sys.controller;

import com.mychat_sys.Vo.UserVo;
import com.mychat_sys.Vo.UserWithDate;
import com.mychat_sys.Vo.userBo;
import com.mychat_sys.bean.ChatMsg;
import com.mychat_sys.bean.FriendsRequest;
import com.mychat_sys.bean.Mail;
import com.mychat_sys.bean.User;
import com.mychat_sys.idmaker.Sid;
import com.mychat_sys.mapper.MyFriendsMapper;
import com.mychat_sys.service.*;
import com.mychat_sys.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    MyLockService myLockService;
    @Autowired
    FriendsRequestService friendsRequestService;
    @Autowired
    MyFriendsService myFriendsService;
    @Autowired
    FastDFSClient fastDFSClient;
    @Autowired
    ChatMsgService chatMsgService;
    @Autowired
    MailService mailService;

    @RequestMapping("/login")
    @ResponseBody
    public myJSONResult Login(String username,String password){
        User userResult = userService.isExistByUsername(username);
        System.out.println(username);
        //用户存在，登陆验证
        if(userResult!=null){
            //判断密码是否正确
            if(!userResult.getPassword().equals(MD5Utils.getPwd(password))) {
                return myJSONResult.errorMsg("密码不正确");
            }
        }
        else{
            return myJSONResult.errorMsg("用户名不存在");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult,userVo);
        return myJSONResult.ok(userVo);
    }

    @RequestMapping("/register")
    @ResponseBody
    public myJSONResult Register(User user) throws IOException {
        if(userService.isExistByUsername(user.getUsername())!=null){
            return myJSONResult.errorMsg("该用户手机已经注册过账号");
        }
        String qrcode_path = "/usr/picture/" + user.getUsername() + "qrcode.png";
        QRCodeUtils.createQRCode(qrcode_path,user.getUsername());
        MultipartFile qrcode_file = FileUtils.fileToMultipart(qrcode_path);
        String qrcode_url = fastDFSClient.uploadQRCode(qrcode_file);
        user.setPassword(MD5Utils.getPwd(user.getPassword()));
        user.setNickname(user.getNickname());
        user.setQrcode(qrcode_url);
        user.setFaceImage("");
        user.setFaceImageBig("");
        user.setId(Sid.nextShort());
        if(userService.insertUser(user)==1){
            return myJSONResult.ok();
        }
        else {
            return myJSONResult.errorMsg("注册失败，请重试");
        }
    }

    @RequestMapping("/updateCode")
    @ResponseBody
    public myJSONResult updateCode(String id,String password) {
        User user = new User();
        user.setId(id);
        user.setPassword(MD5Utils.getPwd(password));
        userService.updatePassword(user);
        return myJSONResult.ok();
    }



    @RequestMapping("/modifyNickname")
    @ResponseBody
    public myJSONResult modifyNickname(String nickname,String my_id){
        if(userService.modifyNickname(nickname,my_id)==1){
             User user = userService.getUserById(my_id);
             UserVo userinfo = new UserVo();
             BeanUtils.copyProperties(user,userinfo);
             return myJSONResult.ok(userinfo);
        }
        else{
            return myJSONResult.errorMsg("修改昵称失败");
        }
    }

    //上传头像
    @RequestMapping("/uploadFaceBase64")
    @ResponseBody
    public myJSONResult uploadFaceBase64(userBo userBo) throws Exception{
        System.out.println(userBo.getUserId());
        //获取图片base64编码
        String Base64 = userBo.getFaceData();
        //设置头像存储路径
        String face_path = "/usr/picture/"+userBo.getUserId()+"Base64";
        //头像base64编码转为文件对象
        FileUtils.base64ToFile(face_path,Base64);
        //文件对象转为multipartFile对象
        MultipartFile multipartFile = FileUtils.fileToMultipart(face_path);
        //获取fastdfs上传路径
        String upload_url = fastDFSClient.uploadBase64(multipartFile);
        String oldFileSmall = "mychat/"+userService.getUserById(userBo.getUserId()).getFaceImage();
        String oldFileBig = "mychat/"+userService.getUserById(userBo.getUserId()).getFaceImageBig();
        //获取头像缩略图路径
        String[] arr = upload_url.split("\\.");
        String upload_thump_url = arr[0]+"_150x150."+arr[1];
        User user = new User();
        UserVo userVo = new UserVo();
        user.setId(userBo.getUserId());
        user.setFaceImage(upload_thump_url);
        user.setFaceImageBig(upload_url);
        User result = userService.updateUserInfo(user);
        if(result!=null){
            fastDFSClient.deleteFile(oldFileSmall);
            fastDFSClient.deleteFile(oldFileBig);
            BeanUtils.copyProperties(result,userVo);
            return myJSONResult.ok(userVo);
        }
        else return myJSONResult.errorMsg("上传头像失败");
    }

//    获取Lock信息
    @RequestMapping("/getLockInfo")
    @ResponseBody
    public myJSONResult getLockInfo(String lock_username){
        User lockInfo = userService.isExistByUsername(lock_username);
        if(lockInfo != null){
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(lockInfo,userVo);
            return myJSONResult.ok(userVo);
        }
        else return myJSONResult.errorMsg("该用户不存在");
    };

//    发送Lock请求
    @RequestMapping("/sentLockRequest")
    @ResponseBody
    public myJSONResult sentLockRequest(String myuserId, String lockId){
        if(myLockService.isAlreadyLock(lockId)){
            return myJSONResult.errorMsg("该用户Locked！");
        }
        if(myLockService.isAlreadySend(myuserId,lockId)){
            return myJSONResult.errorMsg("已经向该用户发送过Lock申请");
        }
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setId(Sid.nextShort());
        friendsRequest.setSendUserId(myuserId);
        friendsRequest.setAcceptUserId(lockId);
        friendsRequest.setRequestDateTime(new Date());
        if(friendsRequestService.insert(friendsRequest) == 1){
            return myJSONResult.ok();
        }
        else return myJSONResult.errorMsg("发送Lock请求失败");

    }

//    获取Lock请求人的信息
    @RequestMapping("/getInfoFromRequest")
    @ResponseBody
    public myJSONResult getInfoFromRequest(String my_id){
        List<UserVo> requestList = userService.getInfoFromRequest(my_id);
        return myJSONResult.ok(requestList);
    }

//    确认接受Lock申请
    @RequestMapping("/confirmLock")
    @ResponseBody
    public myJSONResult confirmLock(String my_id, String lock_id){
        UserWithDate lockInfo = myLockService.confirmLock(my_id, lock_id);
        if(lockInfo != null){
            return myJSONResult.ok(lockInfo);
        }
        else return myJSONResult.errorMsg("同意Lock请求失败");
    }

    @RequestMapping("/getMyLockInfo")
    @ResponseBody
    public myJSONResult getMyLockInfo(String my_id){
        UserWithDate myLockInfo = myLockService.getMyLockInfo(my_id);
        return myJSONResult.ok(myLockInfo);
    }

    @RequestMapping("/deleteLock")
    @ResponseBody
    public myJSONResult deleteLock(String my_id) {
        if(myFriendsService.deleteLock(my_id)!=0){
//            System.out.println(myFriendsService.deleteLock(my_id));
            //删除mail
            mailService.removeMails(my_id);
            return myJSONResult.ok(new String("解除Lock关系成功"));
        }
        else return myJSONResult.errorMsg("解除Lock关系失败");
    }

    @RequestMapping("/getUnreadMsgList")
    @ResponseBody
    public myJSONResult getUnreadMsgList(String my_id){
//        System.out.println(my_id+"来接收未读消息");
        List<ChatMsg> unreadMsgList = chatMsgService.getUnreadMsgList(my_id);
        return myJSONResult.ok(unreadMsgList);
    }

    //保存Mail
    @RequestMapping("/saveMail")
    @ResponseBody
    public myJSONResult saveMail(Mail mail){
        System.out.println(mail);
        mail.setId(Sid.nextShort());
        return myJSONResult.ok(mailService.saveMail(mail).getId());
    }

    //修改Mail
    @RequestMapping("/updateMailById")
    @ResponseBody
    public myJSONResult updateMailById(String content,String id){
//        System.out.println(id);
        return myJSONResult.ok(mailService.updateMailById(content,id));
    }

    //按月查询Mail
    @RequestMapping("/getMailByMonth")
    @ResponseBody
    public myJSONResult getMailByMonth(String month,String userId){
        System.out.println(mailService.getMailByMonth(month,userId));
        return myJSONResult.ok(mailService.getMailByMonth(month,userId));
    }

    //根据id查询Mail
    @RequestMapping("/getMailById")
    @ResponseBody
    public myJSONResult getMailById(String id){
        System.out.println(id);
        return myJSONResult.ok(mailService.getMailById(id).getContent());
    }


    @GetMapping(value="/test")
    public String test(){
        System.out.println(123);
        return "index";
    }







}
