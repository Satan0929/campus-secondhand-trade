<template>
  <div class="home">
    <el-card class="search-card">
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索商品..."
          clearable
          @keyup.enter="loadProducts"
          style="width: 400px; margin-right: 16px"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="selectedCategory" placeholder="选择分类" clearable style="width: 200px; margin-right: 16px">
          <el-option label="全部" value="" />
          <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
        </el-select>
        <el-select v-model="sortBy" style="width: 150px; margin-right: 16px">
          <el-option label="最新发布" value="createTime" />
          <el-option label="价格最低" value="price-asc" />
          <el-option label="价格最高" value="price-desc" />
        </el-select>
        <el-button type="primary" @click="loadProducts">搜索</el-button>
      </div>
    </el-card>

    <div class="product-list">
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in products" :key="product.productId">
          <el-card class="product-card" @click="viewDetail(product.productId)" shadow="hover">
            <div class="product-image">
              <el-icon :size="80"><Picture /></el-icon>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <p class="product-desc">{{ product.description?.substring(0, 50) }}...</p>
              <div class="product-meta">
                <span class="product-price">¥{{ product.price }}</span>
                <span class="product-stock">库存: {{ product.stock }}</span>
              </div>
              <el-tag v-if="product.category" size="small" style="margin-top: 8px">{{ product.category }}</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadProducts"
      />
    </div>

    <el-empty v-if="products.length === 0 && !loading" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const products = ref([])
const categories = ref([])
const keyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('createTime')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (keyword.value) params.keyword = keyword.value
    if (selectedCategory.value) params.category = selectedCategory.value
    
    if (sortBy.value === 'price-asc') {
      params.sortBy = 'price'
      params.sortOrder = 'asc'
    } else if (sortBy.value === 'price-desc') {
      params.sortBy = 'price'
      params.sortOrder = 'desc'
    }
    
    const res = await axios.get('/api/product/list', { params })
    if (res.data.code === 200) {
      products.value = res.data.data.list
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await axios.get('/api/product/categories')
  if (res.data.code === 200) {
    categories.value = res.data.data
  }
}

const viewDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 24px;
}

.search-box {
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
}

.product-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
