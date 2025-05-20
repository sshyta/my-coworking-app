<template>
  <div class="space-item" @mouseenter="hover = true" @mouseleave="hover = false">
    <div class="image-wrapper">
      <img :src="space.image" :alt="space.name">
    </div>
    <div class="info">
      <h3>{{ space.name }}</h3>
      <p class="price">{{ space.price }}</p>
      <ul class="features">
        <li v-for="(feature, i) in space.features" :key="i">{{ feature }}</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  space: {
    type: Object,
    required: true
  }
})

const hover = ref(false)
</script>

<style scoped>
.space-item {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.space-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.image-wrapper {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.image-wrapper:hover img {
  transform: scale(1.05);
}

.info {
  padding: 1.5rem;
}

.info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1d1d1f;
}

.price {
  color: #6e6e73;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.features li {
  list-style: none;
  font-size: 0.9rem;
  color: #86868b;
  position: relative;
  padding-left: 1.2em;
}

.features li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #0071e3;
}
</style>