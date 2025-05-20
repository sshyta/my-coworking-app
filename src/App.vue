<!-- src/App.vue -->
<template>
  <n-config-provider>
    <div class="app-container">
      <header class="header">
        <!-- Навигация -->
        <nav class="navigation">
          <ul>
            <li><router-link to="/">Главная</router-link></li>
            <li><router-link to="/spaces">Пространства</router-link></li>
            <li><router-link to="/about">О нас</router-link></li>
            <li><router-link to="/contact">Контакты</router-link></li>
          </ul>
        </nav>

        <!-- Действия -->
        <div class="actions">
          <button class="search-button">
            <n-icon size="24" :component="Search" />
          </button>
          <button class="cart-button">
            <n-icon size="24" :component="ShoppingCart" />
          </button>
        </div>
      </header>

      <!-- Обёртка с анимацией -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </n-config-provider>
</template>

<script setup lang="ts">
import { Search, CartOutline as ShoppingCart } from '@vicons/ionicons5'
</script>

<style scoped>

body {
  font-family: -apple-system, BlinkMacSystemFont, sans-serif;
  margin: 0;
  background-color: #f5f5f7;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Шапка */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

/* Меню навигации */
.navigation ul {
  list-style: none;
  display: flex;
  gap: 2rem;
  margin: 0;
  padding: 0;
}

.navigation li a {
  text-decoration: none;
  color: #1d1d1f;
  font-weight: 500;
  transition: color 0.3s ease;
}

.navigation li a:hover,
.navigation li a.router-link-active {
  color: #0071e3;
}

/* Кнопки действий */
.actions {
  display: flex;
  gap: 12px;
}

.actions button {
  border: none;
  background: none;
  cursor: pointer;
  padding: 8px;
}

/* Основной контент */
.main-content {
  flex-grow: 1;
}
</style>