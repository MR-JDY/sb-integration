#***系统插件包***
>- Redis插件包，可做单机，也可以使用集群(本项目还未实现)
>- 使用多数据源，对多库进行操作(注意问题：多数据源切换是要考虑断开重连的效率，即连接池运用)
    如果是针对主从数据库操作，建议还是用MyCat这种中间件来进行读写分离，因为从库一旦添加，代码就要添加
>- 使用ELK建立日志系统(未完成)
>- 使用SpringSecurity进行安全策略验证(未完成)
>- 加入RabbitMQ进行异步消息处理
>- 加入邮件发送功能
>- MyCat中间件设置主从数据库，可一台服务器部署多个端口作为主从库测试
>- 加入Activiti作为工作流