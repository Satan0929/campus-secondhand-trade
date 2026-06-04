import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Publish from '../views/Publish.vue'
import Cart from '../views/Cart.vue'
import Orders from '../views/Orders.vue'
import MyProducts from '../views/MyProducts.vue'
import Profile from '../views/Profile.vue'
import Favorite from '../views/Favorite.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/product/:id', component: ProductDetail },
  { path: '/publish', component: Publish },
  { path: '/cart', component: Cart },
  { path: '/orders', component: Orders },
  { path: '/my-products', component: MyProducts },
  { path: '/profile', component: Profile },
  { path: '/favorite', component: Favorite }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
