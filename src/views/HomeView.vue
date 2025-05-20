<template>
  <div class="home-view">
    <!-- Hero Section -->
    <section class="hero" ref="heroSection">
      <h1 class="hero-title">Работайте. <span class="gradient-text">Вдохновляйтесь.</span></h1>
      <p class="hero-subtitle">Премиальные коворкинги с атмосферой Apple Store</p>
      <button class="cta-button" @click="scrollToSpaces">Найти пространство</button>
      <div class="scroll-hint" @click="scrollToSpaces">
        <n-icon size="24" :component="ChevronDown" />
      </div>
    </section>

    <!-- Spaces Gallery -->
    <section class="gallery-section" ref="spacesSection">
      <h2 class="section-title">Популярные пространства</h2>
      <div class="gallery-grid">
        <SpaceItem v-for="space in featuredSpaces" :key="space.id" :space="space" />
      </div>
    </section>

    <!-- Benefits Section -->
    <section class="benefits-section">
      <h2 class="section-title">Почему выбирают нас</h2>
      <div class="benefits-grid">
        <BenefitItem
          v-for="benefit in benefits"
          :key="benefit.title"
          :icon="benefit.icon"
          :title="benefit.title"
          :description="benefit.description"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { NButton, NH2, NIcon } from 'naive-ui' 
import { ChevronDown } from '@vicons/ionicons5'
import SpaceItem from '@/components/SpaceItem.vue'
import BenefitItem from '@/components/BenefitItem.vue'
import gsap from 'gsap'
import { ref, onMounted } from 'vue'

const heroSection = ref<HTMLElement | null>(null)
const spacesSection = ref<HTMLElement | null>(null)

// Данные для примера
const featuredSpaces = ref([
  {
    id: 1,
    name: "Silent Pod",
    price: "₽1,200/день",
    image: "/spaces/silent-pod.jpg",
    features: ["Тихая зона", "MacBook Pro M3", "Кофе включён"]
  },
  {
    id: 2,
    name: "Creative Loft",
    price: "₽1,800/день",
    image: "/spaces/creative-loft.jpg",
    features: ["4K монитор", "Фотостудия", "Терраса"]
  }
])

const benefits = ref([
  {
    icon: 'speed',
    title: "Скорость",
    description: "Wi-Fi 10 Гбит/с и розетки везде"
  },
  {
    icon: 'design',
    title: "Дизайн",
    description: "Пространства в стиле Apple Park"
  },
  {
    icon: 'community',
    title: "Комьюнити",
    description: "События и нетворкинг"
  }
])

// Плавный скролл
const scrollToSpaces = () => {
  spacesSection.value?.scrollIntoView({ behavior: 'smooth' })
}

// Анимация появления
onMounted(() => {
  gsap.from(heroSection.value!, {
    opacity: 0,
    y: 60,
    duration: 1.2,
    ease: 'power3.out'
  })
})
</script>

<style scoped lang="scss">
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;

.home-view {
  font-family: $font-family;
  background-color: #f5f5f7;
  color: #1d1d1f;
  line-height: 1.6;
  max-width: 1440px;
  margin: 0 auto;
}

/* Hero */
.hero {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 0 20px;
  position: relative;
  background: linear-gradient(180deg, #fcfcfd 0%, #fff 100%);
}

.hero-title {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 1rem;
  line-height: 1.1;
}

.gradient-text {
  background: linear-gradient(90deg, #0071e3 0%, #a2d2ff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-subtitle {
  font-size: 1.5rem;
  color: #86868b;
  margin-bottom: 2rem;
  max-width: 600px;
}

.cta-button {
  padding: 0.8rem 2rem;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 999px;
  border: none;
  background: #0071e3;
  color: white;
  cursor: pointer;
  transition: background 0.3s ease;
}

.cta-button:hover {
  background: #005bb5;
}

.scroll-hint {
  position: absolute;
  bottom: 40px;
  animation: bounce 2s infinite;
  cursor: pointer;
  color: #86868b;
}

/* Section Title */
.section-title {
  font-size: 2.5rem;
  text-align: center;
  margin-bottom: 4rem;
  font-weight: 600;
  color: #1d1d1f;
}

/* Gallery Grid */
.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

/* Benefits Grid */
.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1100px;
  margin: 0 auto;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-15px); }
  60% { transform: translateY(-8px); }
}
</style>