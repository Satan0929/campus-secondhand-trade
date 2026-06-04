<template>
  <div class="profile">
    <el-card class="profile-card">
      <template #header>
        <h2>个人中心</h2>
      </template>
      <el-descriptions :column="2" border v-if="user">
        <el-descriptions-item label="用户ID">{{ user.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user.role === 0 ? '普通用户' : '管理员' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ user.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="profile-actions" v-if="user">
        <el-button type="primary" @click="logout">退出登录</el-button>
      </div>
      <el-empty v-else description="请先登录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const user = ref(null)

const loadUserInfo = async () => {
  try {
    const res = await axios.get('/api/user/info')
    if (res.data.code === 200) {
      user.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('获取用户信息失败')
  }
}

const logout = async () => {
  try {
    await axios.post('/api/user/logout')
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error('退出失败')
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadUserInfo()
})
</script>

<style scoped>
.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  margin: 0;
  color: #303133;
}

.profile-actions {
  margin-top: 24px;
  text-align: center;
}
</style>
