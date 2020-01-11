package com.start.controller;


import com.start.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by 追梦1819 on 2019-05-15.
 */
@RestController
public class BookController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // 查询所有的书籍
    @GetMapping("/book/queryBooks")
    public List<Book> queryBooks(){
        String sql = "select * from book ";
        return jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper<>(Book.class));
    }
    // 根据id查询书籍
    @GetMapping("/book/{id}")
    public Book queryBookById(@PathVariable Long id){
        String sql = "select * from book where id = ?";
        return jdbcTemplate.queryForObject(sql,new Object[]{id},new BeanPropertyRowMapper<>(Book.class));
    }
    // 新增书籍
    @PostMapping("/book/save")
    public int saveBook(@RequestBody Book book){
        String sql = "insert into book(book_name,book_price,book_author) values(?,?,?)";
        return jdbcTemplate.update(sql,book.getBookName(),book.getBookPrice(),book.getBookAuthor());
    }
    // 删除书籍
    @GetMapping("/book/delete/{id}")
    public int deleteBook(@PathVariable Long id){
        String sql = "delete from book where id = ?";
        return jdbcTemplate.update(sql,id);
    }
}