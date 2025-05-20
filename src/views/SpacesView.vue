<template>
  <div class="spaces-view">
    <!-- Навигация -->
    <nav class="navigation">
      <ul>
        <li><a href="#all-spaces">Все пространства</a></li>
        <li><a href="#private-spaces">Приватные зоны</a></li>
        <li><a href="#meeting-rooms">Мероприятия</a></li>
        <li><a href="#community-areas">Комьюнити-зоны</a></li>
      </ul>
    </nav>

    <!-- Главный контент -->
    <main class="content">
      <!-- Раздел "Все пространства" -->
      <section id="all-spaces" class="section">
        <h2>Все доступные пространства</h2>
        <SpaceItem v-for="space in spaces" :key="space.id" :space="space" />
      </section>

      <!-- Раздел "Приватные зоны" -->
      <section id="private-spaces" class="section">
        <h2>Приватные зоны</h2>
        <SpaceItem v-for="space in privateSpaces" :key="space.id" :space="space" />
      </section>

      <!-- Раздел "Мероприятия" -->
      <section id="meeting-rooms" class="section">
        <h2>Мероприятия</h2>
        <SpaceItem v-for="space in meetingRooms" :key="space.id" :space="space" />
      </section>

      <!-- Раздел "Комьюнити-зоны" -->
      <section id="community-areas" class="section">
        <h2>Комьюнити-зоны</h2>
        <SpaceItem v-for="space in communityAreas" :key="space.id" :space="space" />
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import SpaceItem from '@/components/SpaceItem.vue'

// Демо-данные
const spaces = [
  {
    id: 1,
    name: 'Silent Pod',
    price: '₽1,200 / день',
    image: '/spaces/silent-pod.jpg',
    features: ['Тихая зона', 'MacBook Pro M3', 'Кофе включён']
  },
  // Другие пространства...
]

const privateSpaces = [
  {
    id: 2,
    name: 'Private Office',
    price: '₽2,500 / день',
    image: '/spaces/private-office.jpg',
    features: ['Закрытая комната', 'Бесплатный Wi-Fi', 'Персональная встреча']
  },
  // Другие приватные зоны...
]

const meetingRooms = [
  {
    id: 3,
    name: 'Event Room',
    price: '₽5,000 / день',
    image: '/spaces/event-room.jpg',
    features: ['Проектор', 'Звуковая система', 'Ресторан на месте']
  },
  // Другие комнаты для мероприятий...
]

const communityAreas = [
  {
    id: 4,
    name: 'Community Lounge',
    price: 'Бесплатно',
    image: '/spaces/community-lounge.jpg',
    features: ['Фуд-корта', 'События', 'Сетевые мероприятия']
  },
  // Другие комьюнити-зоны...
]
</script>

<style scoped lang="scss">
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;

.spaces-view {
  font-family: $font-family;
  background-color: #f5f5f7;
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;

  ul {
    list-style: none;
    padding: 0;
    display: flex;
    gap: 2rem;
  }

  li {
    font-size: 1.1rem;
    font-weight: 500;
    color: #1d1d1f;
  }
}

.content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.section {
  h2 {
    font-size: 2rem;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 2rem;
  }
}
</style>