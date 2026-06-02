<template>
  <div class="orders">
    <el-card>
      <template #header>
        <h2>我的订单</h2>
      </template>
      <el-table :data="orders" v-if="orders.length > 0">
        <el-table-column prop="orderId" label="订单ID" width="120" />
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalPrice" label="总价" width="120">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" size="small" @click="payOrder(row.orderId)">付款</el-button>
            <el-button v-if="row.status === 1" type="success" size="small" @click="completeOrder(row.orderId)">确认收货</el-button>
            <el-button v-if="row.status === 0 || row.status === 1" size="small" @click="cancelOrder(row.orderId)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无订单" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const orders = ref([])

const statusText = (status) => {
  const map = { 0: '待付款', 1: '已付款', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

const loadOrders = async () => {
  try {
    const res = await axios.get('/api/order/my')
    if (res.data.code === 200) {
      orders.value = res.data.data.list
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const payOrder = async (orderId) => {
  try {
    await axios.put(`/api/order/${orderId}/pay`)
    ElMessage.success('付款成功')
    loadOrders()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const completeOrder = async (orderId) => {
  try {
    await axios.put(`/api/order/${orderId}/complete`)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.put(`/api/order/${orderId}/cancel`)
    ElMessage.success('已取消')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadOrders()
})
</script>

<style scoped>
h2 {
  margin: 0;
  color: #303133;
}
</style>
