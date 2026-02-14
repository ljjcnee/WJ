package com.gm.wj.controller;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookService;
import com.gm.wj.service.BorrowRecordService;
import com.gm.wj.service.UserService;
import com.gm.wj.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
public class LibraryController {
    @Autowired
    BookService bookService;

    @Autowired
    BorrowRecordService borrowRecordService;

    @Autowired
    UserService userService;

    @GetMapping("/api/books")
    public Result listBooks() {
        return ResultFactory.buildSuccessResult(bookService.list());
    }

    @PostMapping("/api/admin/content/books")
    public Result addOrUpdateBooks(@RequestBody @Valid Book book) {
        bookService.addOrUpdate(book);
        return ResultFactory.buildSuccessResult("修改成功");
    }

    @PostMapping("/api/admin/content/books/delete")
    public Result deleteBook(@RequestBody @Valid Book book) {
        bookService.deleteById(book.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }

    @GetMapping("/api/search")
    public Result searchResult(@RequestParam("keywords") String keywords) {
        if ("".equals(keywords)) {
            return ResultFactory.buildSuccessResult(bookService.list());
        } else {
            return ResultFactory.buildSuccessResult(bookService.Search(keywords));
        }
    }

    @GetMapping("/api/categories/{cid}/books")
    public Result listByCategory(@PathVariable("cid") int cid) {
        if (0 != cid) {
            return ResultFactory.buildSuccessResult(bookService.listByCategory(cid));
        } else {
            return ResultFactory.buildSuccessResult(bookService.list());
        }
    }

    @PostMapping("/api/admin/content/books/covers")
    public String coversUpload(MultipartFile file) {
        String folder = "D:/workspace/img";
        File imageFolder = new File(folder);
        File f = new File(imageFolder, StringUtils.getRandomString(6) + file.getOriginalFilename()
                .substring(file.getOriginalFilename().length() - 4));
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            file.transferTo(f);
            String imgURL = "http://localhost:8443/api/file/" + f.getName();
            return imgURL;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 核心安全修复：借阅图书
    @PostMapping("/api/borrow")
    public Result borrowBook(@RequestBody Map<String, Integer> request) {
        // 1. 获取当前登录用户会话
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        // 2. 查询真实身份
        String username = subject.getPrincipal().toString();
        User user = userService.getByName(username);

        int uid = user.getId();
        int bid = request.get("bid");

        String res = borrowRecordService.borrow(uid, bid);

        if ("success".equals(res)) {
            return ResultFactory.buildSuccessResult("借阅成功");
        } else {
            return ResultFactory.buildFailResult(res);
        }
    }

    // 核心安全修复：我的书架 (不再轻信前端传来的 uid，而是通过 Shiro 拿真实身份)
    @GetMapping("/api/mybooks")
    public Result getMyBooks() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        // 直接从后端 Session 中拿到当前真正的登录账号
        String username = subject.getPrincipal().toString();
        User user = userService.getByName(username);

        // 用真实的 UID 去查数据库
        return ResultFactory.buildSuccessResult(borrowRecordService.getMyBooks(user.getId()));
    }

    @PostMapping("/api/return")
    public Result returnBook(@RequestBody Map<String, Integer> request) {
        int id = request.get("id");
        borrowRecordService.returnBook(id);
        return ResultFactory.buildSuccessResult("还书成功");
    }
}