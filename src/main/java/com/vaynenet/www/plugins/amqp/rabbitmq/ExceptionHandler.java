package com.vaynenet.www.plugins.amqp.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

/**
 * Created by Joe on 2019/12/4.
 */
@Slf4j
public class ExceptionHandler implements RabbitListenerErrorHandler{
    @Override
    public Object handleError(Message message, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {
        log.error("监听信息[{}]出现问题,错误信息：[{}]",message.toString(),e.getMessage());
        return null;
    }
}
