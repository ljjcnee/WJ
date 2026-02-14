package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evan
 * @date 2019/4
 */
@Service
public class BookService {
    @Autowired
    BookDAO bookDAO;
    @Autowired
    CategoryService categoryService;

    public List<Book> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return bookDAO.findAll(sort);
    }

    public void addOrUpdate(Book book) {
        bookDAO.save(book);
    }

    public void deleteById(int id) {
        bookDAO.deleteById(id);
    }

    public List<Book> listByCategory(int cid) {
        Category category = categoryService.get(cid);
        return bookDAO.findAllByCategory(category);
    }

    public List<Book> Search(String keywords) {
        return bookDAO.findAllByTitleLikeOrAuthorLike('%' + keywords + '%', '%' + keywords + '%');
    }

    // ↓↓↓ 我们新加的方法 ↓↓↓

    // 1. 根据 ID 查询书籍 (借书、还书时需要)
    public Book getById(int id) {
        // orElse(null) 表示如果没查到，就返回 null
        return bookDAO.findById(id).orElse(null);
    }

    // 2. 统计书籍总数 (Dashboard 仪表盘需要)
    public long count() {
        return bookDAO.count();
    }
}