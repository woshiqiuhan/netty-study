Netty 通过 WebSocket 编程实现服务端与客户端的长连接

- Http 协议时无状态的，客户端对服务端的请求连接在资源传输过后断开，下一次请求会重新请求连接
- 要求实现基于 WebSocket 的长连接的全双工交互
- 改变 Http 协议的多次请求约束，实现长连接，服务端可以发送消息给客户端
- 客户端与服务端可以相互感知对方的连接状态