package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordDAO extends JpaRepository<BorrowRecord, Integer> {

    // 1. 根据【图书对象】和【借阅状态】查询真实流水 (用于后台展开面板)
    List<BorrowRecord> findAllByBookAndStatus(Book book, int status);

    // 2. 根据【用户ID】和【借阅状态】查询的方法 (用于前台"我的借阅")
    List<BorrowRecord> findAllByUidAndStatus(int uid, int status);

    // 👑 核心修复：补上这个漏掉的统计方法！
    // 用于统计全馆目前有多少书处于 "status" 状态 (例如查 status=0 即为当前借出总数)
    int countByStatus(int status);

}