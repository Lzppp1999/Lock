window.app = {
	
	/**
	 * netty服务后端发布的url地址
	 */
	nettyServerUrl: 'ws://139.224.210.78:8084/ws',
	
	/**
	 * 后端服务发布的url地址
	 */
	serverUrl: "http://139.224.210.78:8088/mychat_sys/",
	
	/**
	 * 图片服务器的url地址
	 */
	imgServerUrl: 'http://139.224.210.78:88/mychat/',
	
	/**
	 * 判断字符串是否为空
	 * @param {Object} str
	 * true：不为空
	 * false：为空c
	 */
	isNotNull: function(str) {
		if (str != null && str != "" && str != undefined) {
			return true;
		}
		return false;
	},
	
	/**
	 * 封装消息提示框，默认mui的不支持居中和自定义icon，所以使用h5+
	 * @param {Object} msg
	 * @param {Object} type
	 */
	showToast: function(msg,type) {
	
		plus.nativeUI.toast(msg,{icon:'../images/'+type+'.png',verticalAlign:'center'});
		
	},
	/**
	 * 保存用户的全局对象
	 */
	setUserGlobalInfo: function(user){
		var userInfoStr = JSON.stringify(user);
		plus.storage.setItem("userInfo",userInfoStr);
	},
	/**
	 * 获取用户的全局对象
	 */
	getUserGlobalInfo:function(){
		var userInfoStr = plus.storage.getItem("userInfo");
		return JSON.parse(userInfoStr);
	},
	/**
	 * 退出登录
	 */
	userLogout:function(){
		plus.storage.removeItem("userInfo");
		plus.storage.removeItem("contactList");
		//plus.webview.currentWebview().hide();
		plus.webview.close("index");
	},
	
	//保存用户的联系人列表
	setContactList:function(myFriendList){
		var contactListStr = JSON.stringify(myFriendList);
		plus.storage.setItem("contactList",contactListStr);
	},
	
	//删除本地Lock信息
	removeLock:function(){
		plus.storage.removeItem("contactList");
	},
	/**
	 * 获取本地缓存中的联系人列表
	 */
	getContactList: function() {
		var contactListStr = plus.storage.getItem("contactList");
		
		if (!this.isNotNull(contactListStr)) {
			return [];
		}
		
		return JSON.parse(contactListStr);
	},
	getFriendFromContactList:function(friendId){
		//获取联系人列表的本地缓存
		var contactListStr = plus.storage.getItem("contactList");
		if(this.isNotNull(contactListStr)){
			//不为空，则把用户信息返回
			var contactList = JSON.parse(contactListStr);
			for(var i = 0;i<contactList.length;i++){
				var friend = contactList[i];
				if(friend.friendUserId == friendId){
					return friend;
					break;
				}
			}
		}else{
			return null;
		}
		
	},
	/**
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} flag 判断本条消息是我发送的，还是朋友发送的  1:我  2： 朋友
	 * @param {Object} type 0:文本消息 1:语音消息	 
	 * */
	saveUserChatHistory:function(myId,friendId,msg,flag,type){
		var me = this;
		var chatKey = "chat-"+myId+"-" + friendId;
		//从本地缓存获取聊天记录是否存在
		var chatHistoryListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天对象的变量
		var chatHistoryList;
		if(me.isNotNull(chatHistoryListStr)){
			chatHistoryList = JSON.parse(chatHistoryListStr);
		}else{
			//如果为空，赋一个空的list
			chatHistoryList = [];
		}
		
		//构建聊天记录对象
		var singleMsg = new me.ChatHistory(myId,friendId,msg,flag,type);
		
		//向list 中追加msg对象
		chatHistoryList.push(singleMsg);
		
		//写入本地缓存
		plus.storage.setItem(chatKey,JSON.stringify(chatHistoryList));
		
	},
	
	getUserChatHistory:function(myId,friendId){
		var me = this;
		var chatKey = "chat-"+myId+"-" + friendId;
		//从本地缓存获取聊天记录是否存在
		var chatHistoryListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天对象的变量
		var chatHistoryList;
		if(me.isNotNull(chatHistoryListStr)){
			chatHistoryList = JSON.parse(chatHistoryListStr);
		}else{
			//如果为空，赋一个空的list
			chatHistoryList = [];
		}
		return chatHistoryList;
	},
	/**
	 * 删除当前登录用户和朋友的聊天记录
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	deleteUserChatHistory:function(myId,friendId){
		var chatKey = "chat-"+myId+"-" + friendId;
		plus.storage.removeItem(chatKey);
	},
	/**
	 * 聊天记录的快照，仅仅保存每次和朋友聊天的最后一条消息
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead 
	 * @param {Object} type 0:文本消息 1:语音消息
	 */
	saveUserChatSnapshot:function(myId,friendId,msg,isRead,type){
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		//从本地缓存获取聊天快照记录是否存在
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天快照对象的变量
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)){
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
			//循环快照list，并且判断每个元素是否包含friendId，如果匹配，则删除
			for(var i = 0;i<chatSnapshotList.length;i++){
				if(chatSnapshotList[i].friendId == friendId){
					//删除已经存在的friendId 所对应的快照对象
					chatSnapshotList.splice(i,1);
					break;
				}
			}
		}else{
			//如果为空，赋一个空的list
			chatSnapshotList = [];
		}
		
		//构建聊天快照对象
		var singleMsg;
		if(type == 0){
			singleMsg = new me.CHatSnapshot(myId,friendId,msg,isRead);
		}else if(type == 1){
			singleMsg = new me.CHatSnapshot(myId,friendId,"[语音消息]",isRead);
		}
		//向list 中追加snap对象
		chatSnapshotList.unshift(singleMsg);
		
		//写入本地缓存
		plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList));
		
	},
	getUserChatSnapshot:function(myId){
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		//从本地缓存中获取聊天快照的list
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天快照对象的变量
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)){
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
		}else{
			chatSnapshotList = [];
		}
		return chatSnapshotList;
	},
	readUserChatSnapShot:function(myId,friendId){
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		//从本地缓存获取聊天快照记录是否存在
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天快照对象的变量
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)){
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
			//循环快照list，并且判断每个元素是否包含friendId，如果匹配，则删除
			for(var i = 0;i<chatSnapshotList.length;i++){
				var item = chatSnapshotList[i];
				if(item.friendId == friendId){
					item.isRead = true;//标记为已读
					//替换原有快照
					chatSnapshotList.splice(i,1,item);
					break;
				}
			}
			//替换原有的快照列表
			plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList));
		}else{
			return;
		}
	},
	/**
	 * 删除本地的聊天快照记录
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	deleteUserChatSnapshot:function(myId,friendId){
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		//从本地缓存获取聊天快照记录是否存在
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天快照对象的变量
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)){
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
			//循环快照list，并且判断每个元素是否包含friendId，如果匹配，则删除
			for(var i = 0;i<chatSnapshotList.length;i++){
				var item = chatSnapshotList[i];
				if(item.friendId == friendId){
					//替换原有快照
					chatSnapshotList.splice(i,1);
					break;
				}
			}
		}else{
			return;
		}
		//替换原有的快照列表
		plus.storage.setItem(chatKey,JSON.stringify(chatSnapshotList));
	},
	//和后段枚举一一对应
	CONNECT: 1,    // "第一次(或重连)初始化连接"
	CHAT: 2,       // "聊天消息"),	
	SIGNED: 3,     // "消息签收"),
	KEEPALIVE: 4,  //"客户端保持心跳"),
	PULL_FRIEND: 5,// "拉取好友");
	UNLOCK: 6,// "解除lock"
	uploadMail: 7,// "上传mail"
	/**
	 * 和后端的ChatMsg 聊天模型对象保持一致
	 * @param {Object} senderId
	 * @param {Object} receiverId
	 * @param {Object} msg
	 * @param {Object} msgId
	 * @param {Object} type 0：文本消息 1：语音消息
	 */
	ChatMsg: function(senderId,receiverId,msg,msgId,type){
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msg = msg;
		this.msgId = msgId;
		this.type = type;
	},
	/**构建消息DataContent模型对象
	 * @param {Object} action
	 * @param {Object} chatMsg
	 * @param {Object} extend
	 */
	DataContent: function(action,chatMsg,extend){
		this.action = action;
		this.chatMsg = chatMsg;
		this.extend = extend;
	},
	ChatHistory: function(myId,friendId,msg,flag,type){
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.flag = flag;
		this.type = type;
	},
	/**
	 * 创建快照对象的函数
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead 用于判断消息是否为已读还是未读消息
	 */
	CHatSnapshot:function(myId,friendId,msg,isRead){
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.isRead = isRead;
	},
	/**
	
	* 将base64转换为文件
	
	* @param dataurl base64格式数据
	
	* @param filename 文件名
	
	* @param filetype 文件类型
	
	* @returns {File} 二进制流文件
	
	*/
	
	dataURLtoFile:function(dataurl,filename) {
	    var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
	    while (n--) {
	        u8arr[n] = bstr.charCodeAt(n);
	    }
	    return new File([u8arr], filename, {
	        type: mime
	    });
	},
	
	/**
	   * desc: base64对象转blob文件对象
	   * @param base64  ：数据的base64对象
	   * @param fileType  ：文件类型 mp3等;
	   * @returns {Blob}：Blob文件对象
	   */
	dataURItoBlob:function(dataURI) {
	        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]; // mime类型
	        var byteString = atob(dataURI.split(',')[1]); //base64 解码
	        var arrayBuffer = new ArrayBuffer(byteString.length); //创建缓冲数组
	        var intArray = new Uint8Array(arrayBuffer); //创建视图
	
	        for (var i = 0; i < byteString.length; i++) {
	            intArray[i] = byteString.charCodeAt(i);
	        }
	        return new Blob([intArray], {type: mimeString});
	},
	/**
	 * @param {Object} writerId
	 * @param {Object} readerId
	 * @param {Object} content
	 * @param {Object} dateStr
	 * @param {Object} month
	 */
	Mail: function(writerId,readerId,content,dateStr,month){
		this.writerId = writerId;
		this.readerId = readerId;
		this.content = content;
		this.dateStr = dateStr;
		this.month = month;
	},
	/**
	 * @param {Object} myId
	 * @param {Object} dateStr
	 * @param {Object} mail_id
	 */
	saveMailSnapList: function(myId,dateStr,mail_id){
		var me = this;
		var mailKey = "mail-" + myId;
		//从本地缓存获取聊天快照记录是否存在
		var mailSnapListStr = plus.storage.getItem(chatKey);
		
		//用于存储本地聊天快照对象的变量
		var mailSnapList;
		if(me.isNotNull(mailSnapListStr)){
			mailSnapList = JSON.parse(mailSnapListStr);
		}else{
			//如果为空，赋一个空的list
			mailSnapList = [];
		}
		
		//构建Mail快照对象
		var singleMailSnap = new me.MailSnap(dateStr,mail_id);
		
		//向list 中追加snap对象
		mailSnapList.unshift(singleMsg);
		
		//写入本地缓存
		plus.storage.setItem(mailKey,JSON.stringify(mailSnapList));
	},
	/**
	 * @param {Object} dateStr
	 * @param {Object} mail_id
	 */
	MailSnap: function(dateStr,mail_id){
		this.dateStr = dateStr;
		this.mail_id = mail_id;
	},
	//去除前后空格
	trim: function(str)
	{ 
	  return str.replace(/(^\s*)|(\s*$)/g, ""); 
	},
	savePlan: function(plan){
		plus.storage.setItem("plan",plan);
	},
	getPlan: function(){
		return plus.storage.getItem("plan");
	},
	saveDetail: function(detail){
		plus.storage.setItem("detail",detail);
	},
	getDetail: function(){ 
		return plus.storage.getItem("detail");
	}
    
	
	
}
