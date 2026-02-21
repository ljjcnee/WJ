<template>
  <el-table :data="list" style="width: 100%;padding-top: 15px;">
    <el-table-column label="流水号" width="80" align="center">
      <template slot-scope="scope">
        {{ scope.row.id }}
      </template>
    </el-table-column>
    <el-table-column label="借阅书籍" min-width="150" show-overflow-tooltip>
      <template slot-scope="scope">
        {{ scope.row.book ? scope.row.book.title : '未知书籍' }}
      </template>
    </el-table-column>
    <el-table-column label="借阅人" width="100" align="center">
      <template slot-scope="scope">
        <el-tag type="info">{{ scope.row.username || '未知用户' }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="状态" width="100" align="center">
      <template slot-scope="{row}">
        <el-tag :type="row.status === 0 ? 'danger' : 'success'">
          {{ row.status === 0 ? '借出中' : '已归还' }}
        </el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  // 👑 接收真实的近期流水数组
  props: {
    list: {
      type: Array,
      default: () => []
    }
  }
}
</script>
