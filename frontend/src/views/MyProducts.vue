<template>
  <div class="my-products">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <h2>我的商品</h2>
          <el-button type="primary" @click="$router.push('/publish')">发布新商品</el-button>
        </div>
      </template>
      <el-table :data="products" v-if="products.length > 0">
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="warning" size="small" @click="offlineProduct(row.productId)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无商品" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const products = ref([])

const statusText = (status) => {
  const map = { 0: '在售', 1: '已售', 2: '已下架' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'success', 1: 'info', 2: 'warning' }
  return map[status] || 'info'
}

const loadProducts = async () => {
  try {
    const res = await axios.get('/api/product/my')
    if (res.data.code === 200) {
      products.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const offlineProduct = async (productId) => {
  try {
    await axios.put(`/api/product/${productId}/offline`)
    ElMessage.success('已下架')
    loadProducts()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadProducts()
})
</script>

<style scoped>
h2 {
  margin: 0;
  color: #303133;
}
</style>
