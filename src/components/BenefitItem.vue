<template>
  <div class="benefit-item">
    <h4>{{ title }}</h4>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  description: String
})
</script>

<style scoped>
.benefit-item {
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
  text-align: center;
}
</style>