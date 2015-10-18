包括聊天服务器和 Android 客户端

Android 客户端操作流程：点击登录建立 websocket 长连接，成功后发送广播，登录界面收到广播后进入在线联系人列表界面，请求回 json 格式的联系人信息展示在列表里，点击联系人列表进入聊天界面。点击加号显示可发送的更多消息类型，类似QQ底部弹框，使用 ViewPager 嵌套的 GridView 开发。


开发过程中用到的技术点
WebSocket https://github.com/TooTallNate/Java-WebSocket/wiki
android-websockets  https://github.com/codebutler/android-websockets
                    http://zengrong.net/post/2199.htm
                    http://www.zhihu.com/question/20215561

EventBus    源码 https://github.com/greenrobot/EventBus
            使用 http://blog.csdn.net/yuanzeyao/article/details/38174537
            这里也有介绍 http://www.apkbus.com/android-242603-1-1.html
            
用 Gson 解析 json
用 Android-Universal-Image-Loader 处理图片
