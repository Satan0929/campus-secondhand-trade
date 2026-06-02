<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <el-icon><ShoppingBag /></el-icon>
            <span>校园二手交易平台</span>
          </div>
          <div class="nav-links">
            <template v-if="isLoggedIn">
              <el-button text @click="$router.push('/publish')">
                <el-icon><Plus /></el-icon> 发布商品
              </el-button>
              <el-button text @click="$router.push('/cart')">
                <el-icon><ShoppingCart /></el-icon> 购物车
              </el-button>
              <el-button text @click="$router.push('/orders')">
                <el-icon><Document /></el-icon> 我的订单
              </el-button>
              <el-button text @click="$router.push('/my-products')">
                <el-icon><Goods /></el-icon> 我的商品
              </el-button>
              <span class="user-name">{{ user?.username }}</span>
              <el-button type="danger" size="small" @click="logout">退出</el-button>
            </template>
            <template v-else>
              <el-button text @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoggedIn = ref(false)
const user = ref(null)

const checkLogin = async () => {
  const token = localStorage.getItem('token')
  if (token) {
    axios.defaults.headers.common['Authorization'] = token
    try {
      const res = await axios.get('/api/user/info')
      if (res.data.code === 200) {
        isLoggedIn.value = true
        user.value = res.data.data
      }
    } catch (e) {
      localStorage.removeItem('token')
    }
  }
}

const logout = async () => {
  await axios.post('/api/user/logout')
  localStorage.removeItem('token')
  isLoggedIn.value = false
  user.value = null
  router.push('/')
}

onMounted(() => {
  checkLogin()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #409eff;
  color: white;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-links .el-button {
  color: white;
}

.nav-links .el-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-name {
  margin-left: 16px;
}

.el-main {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 20px;
}
</style>
