<template>
  <div class="product-detail" v-if="product">
    <el-card>
      <el-row :gutter="40">
        <el-col :span="12">
          <div class="product-image-large">
            <el-icon :size="200"><Picture /></el-icon>
          </div>
        </el-col>
        <el-col :span="12">
          <h1>{{ product.title }}</h1>
          <div class="price">¥{{ product.price }}</div>
          <el-tag v-if="product.category" style="margin-bottom: 16px">{{ product.category }}</el-tag>
          <p class="description">{{ product.description }}</p>
          <div class="meta">
            <span>库存: {{ product.stock }}</span>
            <span>状态: {{ statusText }}</span>
          </div>
          <div class="actions" style="margin-top: 24px">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" style="margin-right: 16px" />
            <el-button type="primary" size="large" @click="buyNow" :disabled="product.status !== 0">立即购买</el-button>
            <el-button size="large" @click="addToCart" :disabled="product.status !== 0">加入购物车</el-button>
            <el-button size="large" @click="addFavorite" icon="Star">收藏</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const quantity = ref(1)

const statusText = computed(() => {
  const map = { 0: '在售', 1: '已售', 2: '已下架' }
  return map[product.value?.status] || '未知'
})

const loadProduct = async () => {
  const res = await axios.get(`/api/product/${route.params.id}`)
  if (res.data.code === 200) {
    product.value = res.data.data
  }
}

const buyNow = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await axios.post('/api/order', {
      productId: product.value.productId,
      quantity: quantity.value
    })
    if (res.data.code === 200) {
      ElMessage.success('下单成功')
      router.push('/orders')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('下单失败')
  }
}

const addToCart = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await axios.post('/api/cart', {
      productId: product.value.productId,
      quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const addFavorite = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await axios.post('/api/favorite', {
      productId: product.value.productId
    })
    ElMessage.success('已收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-image-large {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 8px;
}

h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 16px;
}

.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 16px;
}

.description {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 16px;
}

.meta {
  color: #909399;
  font-size: 14px;
}

.meta span {
  margin-right: 24px;
}
</style>
