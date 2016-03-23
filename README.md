
### 包含的功能和模块
* Android 客户端：包含 注册、登录、最近会话列表、好友列表、聊天 等界面。  <br/>
* 业务服务器：包括 注册登录等，用的 Python 和 Mysql  <br/>
* 文件服务器：上传下载语音，图片，视频 等，用的 Java Servlet  <br/>
* 聊天服务器：收发消息，加删好友，用的 Java 和 Mysql，后又转为 性能更好的 Netty。  <br/>


### 其他杂乱的知识点
* WebSocket <br/>
  服务器用的 [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket),也可以用 大名鼎鼎，性能超好的号称单机百万轻轻松松 [Netty](http://netty.io/)。<br/>
Android  客户端用的[android-websockets]( https://github.com/codebutler/android-websockets)
    <br/>WebSocket详细的规范可参照 [RFC6455](https://tools.ietf.org/html/rfc6455)，
知乎上的大神讲的很清晰[这里](http://www.zhihu.com/question/20215561),
socket 和 WebSocket 区别 [这里](http://zengrong.net/post/2199.htm)
        
* 网络连接用了 [Volley] (https://github.com/mcxiaoke/android-volley) 和 [OkHttp](http://square.github.io/okhttp/)
* 事件总线 [EventBus](https://github.com/greenrobot/EventBus)
* 图片用了 [Picasso](https://github.com/square/picasso)
* 


