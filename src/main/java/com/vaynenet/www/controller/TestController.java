package com.vaynenet.www.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.vaynenet.www.dao.ArticleDao;
import com.vaynenet.www.entity.Article;
import com.vaynenet.www.plugins.datasources.DataSource;
import com.vaynenet.www.plugins.datasources.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vaynenet.www.plugins.redis.RedisUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Joe on 2019/11/27.
 */
@RestController
@Slf4j
public class TestController {
    private Logger logger = Logger.getLogger(TestController.class);
    @Resource
    private ArticleDao articleDao;
    @Resource
    RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DataSource(value  = "slave")
    @RequestMapping(value = "/getTest")
    public List<Article> getTest(){
        /*if(redisUtil.hasKey("keyOne")){
            redisUtil.del("keyOne");
            logger.debug("存在相应的键值对[key:{keyOne},value:{"+redisUtil.get("keyOne")+"}]");
        }else{
            redisUtil.set("keyOne",111);
            logger.debug("设置成功了新的键值对");
        }*/
        List<Article> articles = articleDao.selectAll();

        return articles;
    }


    @DataSource(value  = "slave")
    @RequestMapping(value = "/insertTest")
    public void insertTest(){
        Article article = new Article();
//        article.setArticleId(222);
        article.setAuthor("aaa"+Math.random());
        article.setName("kkk");
        articleDao.insert(article);
    }


    @DataSource(value  = "master")
    @RequestMapping(value = "/getTest2")
    public List<Article> getTest2(){

        return articleDao.selectAll();
    }

    @RequestMapping(value="/sendMq")
    public String sendMq(){
        rabbitTemplate.convertAndSend("order.createOrderExchange","createOrderKey", JSONUtils.toJSONString("{\"a\":1}"), message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            message.getMessageProperties().setExpiration("10000");
            message.getMessageProperties().setDeliveryTag(System.currentTimeMillis());
            return message;
        });
        log.error("队列时间:"+System.currentTimeMillis());
        return "";
    }
}
