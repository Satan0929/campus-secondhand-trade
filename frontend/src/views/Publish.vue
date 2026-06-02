<template>
  <div class="publish">
    <el-card>
      <template #header>
        <h2>发布商品</h2>
      </template>
      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="商品标题">
          <el-input v-model="form.title" placeholder="请输入商品标题" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="如：书籍、电子产品" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish" size="large">发布</el-button>
          <el-button size="large" @click="$router.push('/')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const form = ref({
  title: '',
  description: '',
  price: 0,
  category: '',
  stock: 1
})

const handlePublish = async () => {
  if (!form.value.title || !form.value.price) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await axios.post('/api/product', form.value)
    if (res.data.code === 200) {
      ElMessage.success('发布成功')
      router.push('/my-products')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('发布失败，请先登录')
    router.push('/login')
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})
</script>

<style scoped>
h2 {
  margin: 0;
  color: #303133;
}
</style>
