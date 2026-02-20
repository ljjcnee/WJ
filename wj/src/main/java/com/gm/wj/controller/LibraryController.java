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

import com.gm.wj.dao.BorrowRecordDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.BorrowRecord;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
public class LibraryController {
    @Autowired
    BookService bookService;

    @Autowired
    BorrowRecordService borrowRecordService;

    @Autowired
    UserService userService;

    @Autowired
    BorrowRecordDAO borrowRecordDAO;

    @Autowired
    UserDAO userDAO;

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

    @PostMapping("/api/borrow")
    public Result borrowBook(@RequestBody Map<String, Integer> request) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

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

    @GetMapping("/api/mybooks")
    public Result getMyBooks() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = subject.getPrincipal().toString();
        User user = userService.getByName(username);

        return ResultFactory.buildSuccessResult(borrowRecordService.getMyBooks(user.getId()));
    }

    @PostMapping("/api/return")
    public Result returnBook(@RequestBody Map<String, Integer> request) {
        int id = request.get("id");
        borrowRecordService.returnBook(id);
        return ResultFactory.buildSuccessResult("还书成功");
    }

    @GetMapping("api/books/{id}/records")
    public Result getBookBorrowRecords(@PathVariable("id") int id) throws Exception {
        // 👑 核心修复：把 bookService.findById(id) 改成了 bookService.get(id)
        // 在大多数 Spring Boot 教程项目中，自带的查询方法通常命名为 get()
        Book book = bookService.getById(id);

        if (book == null) {
            return ResultFactory.buildFailResult("图书不存在");
        }

        List<BorrowRecord> records = borrowRecordDAO.findAllByBookAndStatus(book, 0);

        for (BorrowRecord record : records) {
            try {
                User user = userDAO.findById(record.getUid()).orElse(null);
                if (user != null) {
                    record.setUsername(user.getUsername());
                } else {
                    record.setUsername("未知账号");
                }
            } catch (Exception e) {
                record.setUsername("获取账号失败");
            }
        }

        return ResultFactory.buildSuccessResult(records);
    }

    // 👑 智慧大屏核心接口：统揽全局数据
    @GetMapping("/api/admin/dashboard/statistics")
    public Result getDashboardStatistics() {
        // 1. 馆藏知识库总数
        long bookCount = bookService.count();
        // 2. 注册求知学者总数
        long userCount = userService.count();
        // 3. 当前外借/流转中 (0表示未还)
        long borrowingCount = borrowRecordDAO.countByStatus(0);
        // 4. 累计借阅流水 (JPA自带的count方法，统计表里所有记录)
        long totalBorrowCount = borrowRecordDAO.count();

        // 将数据打包给前端 (这里直接写全路径，防止你少导包报错)
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("bookCount", bookCount);
        stats.put("userCount", userCount);
        stats.put("borrowingCount", borrowingCount);
        stats.put("totalBorrowCount", totalBorrowCount);

        return ResultFactory.buildSuccessResult(stats);
    }
}