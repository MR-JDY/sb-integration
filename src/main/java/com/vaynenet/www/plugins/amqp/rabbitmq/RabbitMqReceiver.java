package com.vaynenet.www.plugins.amqp.rabbitmq;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Joe on 2019/12/3.
 */
@Component
@Slf4j
@Order
public class RabbitMqReceiver {

    @Bean
    public RabbitListenerErrorHandler rabbitListenerErrorHandler(){
        return new RabbitListenerErrorHandler() {
            @Override
            public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {

                log.error("监听异常:"+message);
                return null;
            }
        };
    }
    //支持自动声明绑定，声明之后自动监听队列的队列，此时@RabbitListener注解的queue和bindings不能同时指定，否则报错


    /**
     * 订单创建监听队列
     * @param message
     * @param channel
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory" ,
            bindings ={@QueueBinding(value = @Queue(value = "order.createOrderQueue",durable = "true",
                    autoDelete = "false",
            arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "DL_EXCHANGE"),
                    @Argument(name = "x-dead-letter-routing-key",value = "KEY-ROUT"),
                    //@Argument(name = "x-message-ttl",value = "10000")
            }),
            exchange =@Exchange(value = "order.createOrderExchange",durable = "true",type = "direct"),key = "createOrderKey")})
    public void createOrderQueue(Message message, Channel channel) throws IOException{
        log.debug("收到的消息体1：[{}]",message);
        try {
            int i = 1/0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.error("确认消息队列成功接收");
        }catch (Exception e){
            //在发送Message的时候指定过期时间，到达时间之后会自动进入死信队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            log.error("消息收取失败，返回队列");
        }

    }

    /**
     * 更新库存监听队列
     * @param message
     * @param channel
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory" ,
            bindings ={@QueueBinding(value = @Queue(value = "inventory.updateInvQueue",durable = "true",autoDelete = "false",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange",value = "DL_EXCHANGE"),
                            @Argument(name = "x-dead-letter-routing-key",value = "KEY-ROUT")
                    }),
                    exchange =@Exchange(value = "inventory.updateInvQueue",durable = "true",type = "fanout"),key = "updateInvKey")})
    public void updateInvQueue(Message message, Channel channel){
        log.debug("收到的消息体2：[{}]",message);
    }

    /**
     * 发送失败的消息进入死信队列，进行类似邮件发送或者错误日志进入数据库等操作
     * @param message
     * @param channel
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",bindings ={@QueueBinding(
            value = @Queue(value = "all.dead_exchange",durable = "true",autoDelete = "false"),
            exchange =@Exchange(value = "DL_EXCHANGE",durable = "true",type = "fanout"),key = "KEY-ROUT")})
    public void dlExchange(Message message, Channel channel) throws IOException{
        log.debug("收到死信队列的消息体：[{}]",message);
        log.error("队列时间:"+System.currentTimeMillis());
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
    }


}
