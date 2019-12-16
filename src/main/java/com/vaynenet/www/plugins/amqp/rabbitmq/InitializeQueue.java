package com.vaynenet.www.plugins.amqp.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Joe on 2019/12/3.
 *
 * 根据配置文件自动初始化交换机和队列，并实现自动绑定
 *
 * 废弃理由：
 * 1、QueuesProperties废弃
 */
//@Component
//@Order(value= 10)
@Slf4j
@Deprecated
public class InitializeQueue implements ApplicationRunner{

    @Value("${spring.rabbitmq.autoInitialize}")
    private boolean autoInitialize;
    @Autowired
    public QueuesProperties queuesProperties;
    @Autowired
    public RabbitConfig rabbitConfig;
    @Autowired
    public RabbitAdmin rabbitAdmin;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Assert.notNull(autoInitialize,"The argument spring.rabbitmq.autoInitialize can not be null");
        if(!autoInitialize){
            return;
        }
        init();
    }

    /**
     * 初始化队列和交换机
     */
    public void init(){
        log.info(".....准备初始化队列交换机.....");
        List<QueuesProperties.QueueEntity> mapList = queuesProperties.getQueueList();
        mapList.forEach((entity)->{
            FanoutExchange fanoutExchange = rabbitConfig.createFanoutExchange(entity.getExchange());
            try{
                rabbitAdmin.declareExchange(fanoutExchange);
            }catch (Exception e){
                log.error("已经存在名称为[{}]的交换机",entity.getExchange());
//                return;
            }
            List<String> queues = entity.getQueues();
            queues.forEach((queueName)->{
                Queue queue = rabbitConfig.createQueue(queueName);

                /*try{
                    rabbitAdmin.declareQueue(queue);
                }catch (Exception e){
                    log.error("已经存在名称为[{}]的队列",queueName);
//                    return;
                }*/
                Binding binding = rabbitConfig.binding(queue, fanoutExchange);
                rabbitAdmin.declareBinding(binding);
            });
        });
        log.info(".....完成初始化队列交换机.....");
    }
}
