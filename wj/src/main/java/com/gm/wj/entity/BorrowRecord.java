package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
@Table(name = "borrow_record")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // 借书人的用户ID
    @Column(name = "uid")
    private int uid;

    // 关联的图书 (多对一关系：多条借阅记录对应一本书)
    @ManyToOne
    @JoinColumn(name = "bid")
    private Book book;

    // 借书时间
    @Column(name = "borrow_time")
    private String borrowTime;

    // 还书时间
    @Column(name = "return_time")
    private String returnTime;

    // 借阅状态 (0: 正在借阅未还, 1: 已经归还)
    @Column(name = "status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}