package com.vaynenet.www.plugins.amqp.rabbitmq;

import com.rabbitmq.client.ReturnCallback;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Joe on 2019/12/2.
 * RabbitMQ的配置以及相关的启动、创建
 *
 * Name：交换机名称
 * Durability：是否持久化。如果持久性，则RabbitMQ重启后，交换机还存在
 * Auto-delete：当所有与之绑定的消息队列都完成了对此交换机的使用后，删掉它
 * Arguments：扩展参数
 */
@Configuration
@Slf4j
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Component
public class RabbitConfig implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback{


    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean(name="connectionFactory")
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    /**
     * 消息发送到交换器Exchange后触发回调
     * @param correlationData
     * @param b
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        log.info("进入CorrelationData确认");
    }

    /**
     * 消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）
     * @param message
     * @param i
     * @param s
     * @param s1
     * @param s2
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.info("进入返回信息");
    }
    /**
     * 为能使用自定义队列
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(){
        return new RabbitAdmin(connectionFactory());
    }
    /**
     * 创建队列
     * @param queueName
     * @return
     */
    public Queue createQueue(String queueName){
        return new Queue(queueName,true,true,false,null);
    }

    /**
     * 创建主题交换机
     * @param topicExchangeName
     * @return
     */
    public TopicExchange createTopicExchange(String topicExchangeName){
        return new TopicExchange(topicExchangeName,true,false);
    }

    /**
     * 创建扇形交换机
     * @param fanoutExchangeName
     * @return
     */
    public FanoutExchange createFanoutExchange(String fanoutExchangeName){
       return new FanoutExchange(fanoutExchangeName,true,false);
    }

    /**
     * 创建直连交换机
     * @param directExchangeName
     * @return
     */
    public DirectExchange createDirectExchange(String directExchangeName){
        return new DirectExchange(directExchangeName,true,false);
    }

    /**
     * 创建头交换机
     * @param headersExchange
     * @return
     */
    public HeadersExchange createHeadersExchange(String headersExchange){
        return new HeadersExchange(headersExchange,true,false);
    }

    /**
     * 绑定队列到主题交换机
     * routing key为一个句点号“. ”分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如“stock.usd.nyse”、“nyse.vmw”、“quick.orange.rabbit”
     binding key与routing key一样也是句点号“. ”分隔的字符串
     binding key中可以存在两种特殊字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
     * @param queue
     * @param exchange
     * @param routeKey
     * @return
     */
    public Binding binding(Queue queue,TopicExchange exchange,String routeKey){
        return BindingBuilder.bind(queue).to(exchange).with(routeKey);
    }

    /**
     * 绑定队列到扇形交换机
     * fanout类型的Exchange路由规则非常简单，它会把所有发送到该Exchange的消息路由到所有与它绑定的Queue中。
     * 所以不需要指定路由键
     * @param queue
     * @param exchange
     * @return
     */
    public Binding binding(Queue queue,FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

    /**
     * 绑定队列到头交换机
     * 头交换机使用多个消息属性来代替路由键建立路由规则。通过判断消息头的值能否与指定的绑定相匹配来确立路由规则
     * @param queue
     * @param exchange
     * @param headersMap
     * @return
     */
    public Binding binding(Queue queue, HeadersExchange exchange, Map<String,Object> headersMap) {
        return BindingBuilder.bind(queue).to(exchange).whereAny(headersMap).match();
    }

    /**
     * 绑定队列到直连交换机
     * direct类型的Exchange路由规则也很简单，它会把消息路由到那些binding key与routing key完全匹配的Queue中。
     * @param queue
     * @param exchange
     * @param routeKey
     * @return
     */
    public Binding binding(Queue queue,DirectExchange exchange,String routeKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routeKey);
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(new Jackson2JsonMessageConverter());//数据转换成JSON
        return template;
    }


    /**
     * 自定义消息转换器
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }


}
