// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SpacesView from '@/views/SpacesView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/spaces',
    name: 'spaces',
    component: SpacesView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router