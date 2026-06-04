<template>
  <div class="favorite">
    <el-card>
      <template #header>
        <h2>我的收藏</h2>
      </template>
      <div class="product-list" v-if="favorites.length > 0">
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in favorites" :key="item.product.productId">
            <el-card class="product-card" shadow="hover">
              <div class="product-image" @click="viewDetail(item.product.productId)">
                <el-icon :size="80"><Picture /></el-icon>
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ item.product.title }}</h3>
                <p class="product-desc">{{ item.product.description?.substring(0, 50) }}...</p>
                <div class="product-meta">
                  <span class="product-price">¥{{ item.product.price }}</span>
                  <span class="product-stock">库存: {{ item.product.stock }}</span>
                </div>
                <el-tag v-if="item.product.category" size="small" style="margin-top: 8px">{{ item.product.category }}</el-tag>
                <div class="product-actions">
                  <el-button type="primary" size="small" @click="viewDetail(item.product.productId)">查看详情</el-button>
                  <el-button type="danger" size="small" @click="removeFavorite(item.product.productId)">取消收藏</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <el-empty v-else description="暂无收藏" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const favorites = ref([])

const loadFavorites = async () => {
  try {
    const res = await axios.get('/api/favorite')
    if (res.data.code === 200) {
      favorites.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const removeFavorite = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`/api/favorite/${productId}`)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const viewDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadFavorites()
})
</script>

<style scoped>
h2 {
  margin: 0;
  color: #303133;
}

.product-card {
  margin-bottom: 20px;
}

.product-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
  cursor: pointer;
}

.product-info {
  padding-top: 12px;
}

.product-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  height: 36px;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.product-stock {
  font-size: 13px;
  color: #909399;
}

.product-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
