import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Publish from '../views/Publish.vue'
import Cart from '../views/Cart.vue'
import Orders from '../views/Orders.vue'
import MyProducts from '../views/MyProducts.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/product/:id', component: ProductDetail },
  { path: '/publish', component: Publish },
  { path: '/cart', component: Cart },
  { path: '/orders', component: Orders },
  { path: '/my-products', component: MyProducts }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
