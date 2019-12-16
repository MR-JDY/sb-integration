package com.vaynenet.www.dao;

import com.vaynenet.www.entity.Article;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ArticleDao {
    int insert(Article record);

    List<Article> selectAll();
}