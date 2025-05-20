<template>
  <header class="header">
    <!-- Логотип -->
    <div class="logo">
      <img src="/logo.png" alt="Логотип компании" />
    </div>

    <!-- Навигация -->
    <nav class="navigation">
      <ul>
        <li><a href="#hero">Главная</a></li>
        <li><a href="#spaces">Пространства</a></li>
        <li><a href="#benefits">Почему выбирают нас</a></li>
        <li><a href="#contact">Контакты</a></li>
      </ul>
    </nav>

    <!-- Дополнительные элементы -->
    <div class="actions">
      <button class="search-button">
        <n-icon size="24" :component="Search" />
      </button>
      <button class="cart-button">
        <n-icon size="24" :component="ShoppingCart" />
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Search, CartOutline as ShoppingCart } from '@vicons/ionicons5'
</script>

<style scoped lang="scss">
$header-height: 60px;

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: $header-height;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.logo img {
  height: 30px;
}

.navigation ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  gap: 2rem;
}

.navigation li a {
  font-size: 1rem;
  font-weight: 500;
  color: #1d1d1f;
  text-decoration: none;
  transition: color 0.3s ease;

  &:hover {
    color: #0071e3;
  }
}

.actions button {
  border: none;
  background: none;
  cursor: pointer;
  padding: 0 10px;
}
</style>