
##	HTTP缓存

*	服务器缓存

>	网关缓存、代理缓存等。是让请求走捷径，直接取静态的图片、文件等

*	客户端缓存

>	即本地缓存，不用每一次都请求服务器

##	缓存过程

如图是服务器缓存过程：

![http服务器缓存](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/http/resource/服务端缓存.png)

客户端缓存，则是经过多次缓存验证

![http客户端缓存](https://github.com/mirindalover/SummaryOfProgrammingLearning/blob/master/http/resource/客户端缓存.png)

##	重要的头部

###	缓存策略

*	禁用缓存	

>	pragma:no-store  |		Cache-Control:no-store

*	缓存方式

>	Cache-Control:private|public...|no-cached(相当于max-age=0)

*	过期日期

>	Expires: [Date]	| Cache-Control:max-age=[time]

*	新鲜验证

>	if-modified-since:[date]	|	if-none-match:[E-Tag]
>
>	E-Tag:数据的版本、验证令牌







