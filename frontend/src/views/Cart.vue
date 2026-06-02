<template>
  <div class="cart">
    <el-card>
      <template #header>
        <h2>购物车</h2>
      </template>
      <el-table :data="cartItems" v-if="cartItems.length > 0">
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" size="small" @change="updateQuantity(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="removeItem(row.cartId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="购物车是空的" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const cartItems = ref([])

const loadCart = async () => {
  try {
    const res = await axios.get('/api/cart')
    if (res.data.code === 200) {
      cartItems.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const updateQuantity = async (item) => {
  try {
    await axios.put(`/api/cart/${item.cartId}`, item)
    ElMessage.success('已更新')
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const removeItem = async (cartId) => {
  try {
    await axios.delete(`/api/cart/${cartId}`)
    ElMessage.success('已删除')
    loadCart()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadCart()
})
</script>

<style scoped>
h2 {
  margin: 0;
  color: #303133;
}
</style>
