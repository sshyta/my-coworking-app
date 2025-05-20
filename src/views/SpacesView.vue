<template>
  <div class="spaces-view">
    <main class="content">
      <section class="section">
        <h2>Доступные пространства</h2>
        <div class="spaces-grid">
          <SpaceItem
              v-for="space in allSpaces"
              :key="space.id"
              :space="space"
              @book="openBookingModal"
          />
        </div>
      </section>
    </main>

    <!-- Модальное окно бронирования -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <button class="close-btn" @click="closeModal">×</button>

        <h3>Забронировать {{ selectedSpace?.name }}</h3>

        <div class="space-info">
          <img :src="selectedSpace?.image" :alt="selectedSpace?.name" class="modal-image">
          <div class="details">
            <p><strong>Тип:</strong> {{ selectedSpace?.type }}</p>
            <p><strong>Цена:</strong> {{ selectedSpace?.price }}</p>
            <p><strong>Особенности:</strong> {{ selectedSpace?.features.join(', ') }}</p>
          </div>
        </div>

        <form @submit.prevent="submitBooking" class="booking-form">
          <div class="form-group">
            <label for="name">Ваше имя:</label>
            <input
                type="text"
                id="name"
                v-model="bookingForm.name"
                required
                placeholder="Иван Иванов"
            >
          </div>

          <div class="form-group">
            <label for="email">Email:</label>
            <input
                type="email"
                id="email"
                v-model="bookingForm.email"
                required
                placeholder="example@mail.com"
            >
          </div>

          <div class="form-group">
            <label for="phone">Телефон:</label>
            <input
                type="tel"
                id="phone"
                v-model="bookingForm.phone"
                required
                placeholder="+7 (999) 123-45-67"
            >
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="date">Дата:</label>
              <input
                  type="date"
                  id="date"
                  v-model="bookingForm.date"
                  required
                  :min="new Date().toISOString().split('T')[0]"
              >
            </div>

            <div class="form-group">
              <label for="time">Время:</label>
              <select id="time" v-model="bookingForm.time" required>
                <option value="">Выберите время</option>
                <option v-for="time in availableTimes" :value="time">{{ time }}</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="duration">Длительность:</label>
            <select id="duration" v-model="bookingForm.duration" required>
              <option value="1">1 час</option>
              <option value="2">2 часа</option>
              <option value="4">4 часа</option>
              <option value="8">Полный день (8 часов)</option>
            </select>
          </div>

          <div class="form-group">
            <label for="notes">Дополнительные пожелания:</label>
            <textarea
                id="notes"
                v-model="bookingForm.notes"
                placeholder="Особые требования, дополнительные услуги..."
            ></textarea>
          </div>

          <button type="submit" class="submit-btn">Подтвердить бронь</button>
        </form>
      </div>
    </div>

    <!-- Уведомление об успешном бронировании -->
    <div v-if="showSuccess" class="success-notification">
      <p>Бронь успешно оформлена! Мы отправили подтверждение на {{ bookingForm.email }}</p>
      <button @click="showSuccess = false">Закрыть</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SpaceItem from '../components/SpaceItem.vue'

import silentPod from '@/assets/spaces/silent-pod.jpg'
import privateOffice from '@/assets/spaces/private-office.jpg'
import eventroom from '@/assets/spaces/event-room.jpg'
import communitylounge from '@/assets/spaces/community-lounge.jpg'

// Данные пространств
const allSpaces = [
  {
    id: 1,
    name: 'Silent Pod',
    type: 'Приватная зона',
    price: '₽1,200 / день',
    image: silentPod,
    features: ['Тихая зона', 'MacBook Pro M3', 'Кофе включён']
  },
  {
    id: 2,
    name: 'Private Office',
    type: 'Приватная зона',
    price: '₽2,500 / день',
    image: privateOffice,
    features: ['Закрытая комната', 'Бесплатный Wi-Fi', 'Персональная встреча']
  },
  {
    id: 3,
    name: 'Event Room',
    type: 'Мероприятия',
    price: '₽5,000 / день',
    image: eventroom,
    features: ['Проектор', 'Звуковая система', 'Ресторан на месте']
  },
  {
    id: 4,
    name: 'Community Lounge',
    type: 'Комьюнити-зона',
    price: 'Бесплатно',
    image: communitylounge,
    features: ['Фуд-корта', 'События', 'Сетевые мероприятия']
  }
]

// Состояние модального окна
const showModal = ref(false)
const selectedSpace = ref(null)
const showSuccess = ref(false)

// Доступное время для бронирования
const availableTimes = [
  '09:00', '10:00', '11:00', '12:00',
  '13:00', '14:00', '15:00', '16:00', '17:00'
]

// Форма бронирования
const bookingForm = ref({
  name: '',
  email: '',
  phone: '',
  date: '',
  time: '',
  duration: '1',
  notes: ''
})

// Открыть модальное окно
const openBookingModal = (space) => {
  selectedSpace.value = space
  showModal.value = true
}

// Закрыть модальное окно
const closeModal = () => {
  showModal.value = false
  resetForm()
}

// Сброс формы
const resetForm = () => {
  bookingForm.value = {
    name: '',
    email: '',
    phone: '',
    date: '',
    time: '',
    duration: '1',
    notes: ''
  }
}

// Отправить форму бронирования
const submitBooking = () => {
  console.log('Бронь оформлена:', {
    space: selectedSpace.value,
    booking: bookingForm.value
  })

  // Здесь должна быть логика отправки на сервер
  // Для демо просто показываем уведомление
  showSuccess.value = true
  closeModal()
}
</script>

<style scoped lang="scss">
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
$primary-color: #4a6bff;
$hover-color: #3a5bef;

.spaces-view {
  font-family: $font-family;
  background-color: #f5f5f7;
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.content {
  width: 100%;
}

.section {
  width: 100%;

  h2 {
    font-size: 2rem;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 2rem;
    text-align: center;
  }
}

.spaces-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  width: 100%;
  justify-items: center;
}

/* Стили модального окна */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 30px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);

  h3 {
    font-size: 1.5rem;
    margin-bottom: 20px;
    color: #1d1d1f;
  }
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 1.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 5px;

  &:hover {
    color: #333;
  }
}

.space-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  .modal-image {
    width: 150px;
    height: 150px;
    object-fit: cover;
    border-radius: 8px;
  }

  .details {
    flex: 1;
    p {
      margin: 5px 0;
    }
  }
}

.booking-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;

  label {
    font-weight: 500;
    color: #333;
  }

  input, select, textarea {
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 1rem;

    &:focus {
      outline: none;
      border-color: $primary-color;
      box-shadow: 0 0 0 2px rgba($primary-color, 0.2);
    }
  }

  textarea {
    min-height: 80px;
    resize: vertical;
  }
}

.form-row {
  display: flex;
  gap: 15px;

  .form-group {
    flex: 1;
  }
}

.submit-btn {
  background-color: $primary-color;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  margin-top: 10px;
  transition: background-color 0.2s;

  &:hover {
    background-color: $hover-color;
  }
}

.success-notification {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: #4CAF50;
  color: white;
  padding: 15px 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 15px;
  z-index: 1001;

  button {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
  }
}

@media (max-width: 768px) {
  .spaces-grid {
    grid-template-columns: 1fr;
  }

  .modal {
    width: 95%;
    padding: 20px;
  }

  .space-info {
    flex-direction: column;

    .modal-image {
      width: 100%;
      height: auto;
    }
  }

  .form-row {
    flex-direction: column;
    gap: 15px;
  }
}
</style>