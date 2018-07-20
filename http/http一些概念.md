	
	
###	Socket

	操作系统中也有使用到Socket这个概念用来进行进程间通信，它和通常说的基于TCP/IP的Socket概念十分相似，
	代表了在操作系统中传输数据的两方，只是它不再基于网络协议，而是操作系统本身的文件系统。
###	ServerSocket
	
	监听客户端连接。会创建一个socket(跟其他socket没有区别)，调用listen()来监听端口
	可以通过创建它来开启一个本地服务。第三方 anoHTTPD 就是使用的它 
###	TCP长连接

	HTTP1.1默认使用持久连接（persistent connection），在一个TCP连接上也可以传输多个Request/Response消息对。
	可以使用心跳包的形式保持连接。当然如果协商不需要心跳包也可以一直保持状态不关闭
###	WebSocket

	是为了双向多通信而产生的，相当于与http平级
	也是基于TCP协议，使用http进行握手，定义了不同的header
	不能通过中间人来转发，必须是一个直连
	数据使用帧来传输，不使用http的request/response
		
###	http2.x：
	
	可以并发访问，即可以同时进行多个请求。
	存在的问题：使用TCP协议 丢包的情况会队头阻塞(可靠协议)，重发后阻塞消失
	解决：QUIC协议，使用UDP，加上可靠协议算法
###	https：http+SSL/TSL

	握手阶段
	1、client发送连接请求。
	2、server发送ssl证书(CA颁发的)
	3、client校验证书获取公钥，用公匙加密传输的内容--内容包括：对称加密加密算法、对称加密密钥
	4、server获取加密的内容后，使用私钥解密，获取对称加密的密钥。对称加密告诉client连接成功