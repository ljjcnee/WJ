package com.gm.wj.controller;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.BorrowRecordDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.BorrowRecord;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DashboardController {

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BorrowRecordDAO borrowRecordDAO;

    // 面板数据接口 (保持不变)
    @GetMapping("/api/admin/dashboard/stats")
    public Result getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("books", bookDAO.count());
        stats.put("users", userDAO.count());
        stats.put("borrows", borrowRecordDAO.count());
        stats.put("active", (long) borrowRecordDAO.countByStatus(0));
        return ResultFactory.buildSuccessResult(stats);
    }

    // 👑 核心魔法：新增真实图表数据接口 (纯Java内存聚合，不改数据库，绝对安全！)
    @GetMapping("/api/admin/dashboard/chart-data")
    public Result getChartData() {
        // 1. 拿到所有书和所有的借阅流水
        List<Book> books = bookDAO.findAll();
        List<BorrowRecord> records = borrowRecordDAO.findAll();

        // 2. 统计每本书真实的被借阅次数
        Map<Integer, Integer> heatMap = new HashMap<>();
        for (BorrowRecord br : records) {
            if (br.getBook() != null) {
                int bid = br.getBook().getId();
                // 每出现一次流水，这本书的真实热度就 +1
                heatMap.put(bid, heatMap.getOrDefault(bid, 0) + 1);
            }
        }

        // 3. 组装给前端的最终数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Book book : books) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", book.getId());
            map.put("title", book.getTitle());
            map.put("nums", book.getNums());
            // 提前把分类名提取出来，防止前端拿不到
            map.put("category", book.getCategory() != null ? book.getCategory().getName() : "未分类");
            // 👑 放入真实的借阅次数！
            map.put("heat", heatMap.getOrDefault(book.getId(), 0));
            resultList.add(map);
        }

        return ResultFactory.buildSuccessResult(resultList);
    }

    // 👑 核心魔法：查询全馆真实借阅流水，提供给折线图和实时表格
    @GetMapping("/api/admin/dashboard/recent-records")
    public Result getRecentRecords() {
        List<BorrowRecord> records = borrowRecordDAO.findAll();
        // 遍历流水，把借书人的名字塞进你写的 @Transient 字段里！
        for (BorrowRecord record : records) {
            if (record.getUid() > 0) {
                userDAO.findById(record.getUid()).ifPresent(user -> record.setUsername(user.getUsername()));
            }
        }
        return ResultFactory.buildSuccessResult(records);
    }
}