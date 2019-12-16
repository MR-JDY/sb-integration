package com.vaynenet.www.plugins.amqp.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 2019/12/3.
 *
 * 自动注入相应的队列和路由对应的映射关系实体
 *
 * list对象的属性：
 *      bizCode:业务代码，非必须，可以作为留存的标识字段
 *      routingKey：路由键
 *      exchange: 指定对应的交换机
 *      queues: 绑定的队列（以逗号分隔的数组）
 * 具体绑定实现需要根据自己的交换机类型进行绑定
 *
 *
 * 废弃理由：
 * 1、@RabbitListener会在初始化队列和交换机之前执行，搜索不到队列报错
 * 2、即使在@RabbitListener中加入queuesToDeclare来创建，也无法自动配置队列名称，且无法指定死信队列
 */
//@Component
@Setter
@Getter
//@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
@Deprecated
public class QueuesProperties {

    private List<Map<String,Object>> mapList;

    /**
     * 根据注解注入，获取配置信息
     * @return
     */
    public List<QueueEntity> getQueueList(){
        List<QueueEntity> queueEntities = new ArrayList<QueueEntity>();
        mapList.forEach((queueEntity)->{
            List<String> queueList =   new ArrayList<String>();
            queueEntities.add(new QueueEntity(queueEntity.get("bizCode").toString(),
                    queueEntity.get("routingKey").toString(),
                    queueEntity.get("exchange").toString(),
                    new ArrayList<String>(
                            ((Map)queueEntity.get("queues")).values())
            ));
        });
        return queueEntities;
    }
    @Setter
    @Getter
    class QueueEntity{
        private String bizCode;
        private String routingKey;
        private String exchange;
        private List<String> queues;

        public QueueEntity(String bizCode, String routingKey, String exchange, List<String> queues) {
            this.bizCode = bizCode;
            this.routingKey = routingKey;
            this.exchange = exchange;
            this.queues = queues;
        }
    }
}
