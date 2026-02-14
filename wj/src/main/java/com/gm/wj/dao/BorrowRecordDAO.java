package com.gm.wj.dao;

import com.gm.wj.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRecordDAO extends JpaRepository<BorrowRecord,Integer> {
    // 查某个用户的所有特定状态记录 (用于查我的书架)
    List<BorrowRecord> findAllByUidAndStatus(int uid, int status);

    // 统计特定状态的记录总数 (用于后台查“当前在借”总数)
    int countByStatus(int status);
}