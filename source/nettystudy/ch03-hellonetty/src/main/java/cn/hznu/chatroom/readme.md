Netty 应用实例——群聊系统
实例功能：

- 实现服务端与客户端之间的简单通信 **(非阻塞)**
- 实现多人群聊
- 服务端：可以监测用户上线，下线及消息转发功能
- 客户端：通过 `channel` 可以无阻塞发送消息给其它所有用户，同时可以接受其他用户发送的消息(由服务器转发而来)