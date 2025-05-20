<template>
  <div class="space-item" @click="handleClick">
    <img :src="space.image" :alt="space.name" class="space-image">
    <div class="space-info">
      <h3>{{ space.name }}</h3>
      <p class="price">{{ space.price }}</p>
      <ul class="features">
        <li v-for="(feature, index) in space.features" :key="index">{{ feature }}</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  space: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['book'])

const handleClick = () => {
  emit('book', props.space)
}
</script>

<style scoped>
.space-item {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  background: white;
}

.space-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.space-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.space-info {
  padding: 15px;
}

h3 {
  margin: 0 0 10px 0;
  font-size: 1.2rem;
}

.price {
  font-weight: bold;
  color: #4a6bff;
  margin: 0 0 10px 0;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.features li {
  background: #f5f5f7;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}
</style>