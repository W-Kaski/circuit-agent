<template>
  <div class="algorithm-master-container">
    <div class="header">
      <div class="back-button" @click="goBack">Back</div>
      <h1 class="title">AI Algorithm Master</h1>
      <div class="chat-id">Chat ID: {{ chatId }}</div>
    </div>

    <div class="content-wrapper">
      <div class="chat-area">
        <ChatRoom
            :messages="messages"
            :connection-status="connectionStatus"
            ai-type="algorithm"
            @send-message="sendMessage"
        />
      </div>
    </div>

    <div class="footer-container">
      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ChatRoom from '../components/ChatRoom.vue'
import AppFooter from '../components/AppFooter.vue'
import { chatWithAlgorithmApp } from '../api'

// Set page title and metadata
useHead({
  title: 'Algorithm Master - Circuit Agent',
  meta: [
    {
      name: 'description',
      content: 'Algorithm Master is the algorithm-focused RAG assistant in Circuit Agent.'
    },
    {
      name: 'keywords',
      content: 'AI Algorithm Master, algorithm consulting, AI chat, AI agent'
    }
  ]
})



const router = useRouter()
const messages = ref([])
const chatId = ref('')
const connectionStatus = ref('disconnected')
let eventSource = null

// 生成随机会话ID
const generateChatId = () => {
  return 'algo_' + Math.random().toString(36).substring(2, 10)
}

// 添加消息到列表
const addMessage = (content, isUser) => {
  messages.value.push({
    content,
    isUser,
    time: new Date().getTime()
  })
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true)

  // 连接SSE
  if (eventSource) {
    eventSource.close()
  }

  // 创建一个空的AI回复消息
  const aiMessageIndex = messages.value.length
  addMessage('', false)

  connectionStatus.value = 'connecting'
  eventSource = chatWithAlgorithmApp(message, chatId.value)

  // 监听SSE消息
  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      // 更新最新的AI消息内容，而不是创建新消息
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content += data
      }
    }

    if (data === '[DONE]') {
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }

  // 监听SSE错误
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 生成聊天ID
  chatId.value = generateChatId()

  // 添加欢迎消息
  addMessage('Welcome to AI Algorithm Master. Please tell me your algorithm question, and I will do my best to provide guidance and advice.', false)
})

// 组件销毁前关闭SSE连接
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.algorithm-master-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #0a0a0a !important;
  background-image:
    radial-gradient(circle at 20% 20%, #1a237e 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, #0d47a1 0%, transparent 50%),
    radial-gradient(circle at 40% 60%, #1565c0 0%, transparent 50%),
    linear-gradient(45deg, #0a0a0a 0%, #1a1a2e 25%, #16213e 50%, #0f3460 75%, #0a0a0a 100%) !important;
  background-size: 100% 100%, 100% 100%, 100% 100%, 200% 200%;
  background-position: 0% 0%, 100% 100%, 50% 50%, 0% 0%;
  animation: backgroundShift 20s ease-in-out infinite;
  position: relative;
  overflow-x: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(13, 71, 161, 0.8) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-button {
  font-family: 'Inter', sans-serif;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  color: #00e5ff;
  text-shadow: 0 0 8px rgba(0, 229, 255, 0.5);
}

.back-button:hover {
  color: #4fc3f7;
  text-shadow: 0 0 12px rgba(79, 195, 247, 0.8);
  transform: translateX(-3px);
}

.back-button:before {
  content: '←';
  margin-right: 8px;
  font-size: 18px;
}

.title {
  font-family: 'Inter', sans-serif;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  color: #4fc3f7;
  text-shadow:
    0 0 8px #4fc3f7,
    0 0 16px #4fc3f7,
    0 2px 4px rgba(0, 0, 0, 0.3);
  animation: titleGlow 2s ease-in-out infinite alternate;
}

.chat-id {
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.chat-area {
  flex: 1;
  padding: 20px;
  overflow: hidden;
  position: relative;
  min-height: calc(100vh - 56px - 180px);
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  margin: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.footer-container {
  margin-top: auto;
}

/* 动画关键帧 */
@keyframes backgroundShift {
  0% {
    background-position: 0% 0%, 100% 100%, 50% 50%, 0% 0%;
  }
  25% {
    background-position: 25% 25%, 75% 75%, 75% 25%, 25% 25%;
  }
  50% {
    background-position: 50% 50%, 50% 50%, 25% 75%, 50% 50%;
  }
  75% {
    background-position: 75% 75%, 25% 25%, 50% 25%, 75% 75%;
  }
  100% {
    background-position: 100% 100%, 0% 0%, 75% 50%, 100% 100%;
  }
}

@keyframes titleGlow {
  0% {
    text-shadow:
      0 0 8px #4fc3f7,
      0 0 16px #4fc3f7,
      0 2px 4px rgba(0, 0, 0, 0.3);
  }
  100% {
    text-shadow:
      0 0 12px #4fc3f7,
      0 0 20px #4fc3f7,
      0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

/* 响应式样式 */
@media (max-width: 768px) {
  .header {
    padding: 12px 16px;
  }

  .title {
    font-size: 18px;
  }

  .chat-id {
    font-size: 12px;
  }

  .chat-area {
    padding: 15px;
    min-height: calc(100vh - 48px - 160px);
    margin: 15px;
    border-radius: 15px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 10px 12px;
  }

  .back-button {
    font-size: 14px;
  }

  .title {
    font-size: 16px;
  }

  .chat-id {
    display: none;
  }

  .chat-area {
    padding: 12px;
    min-height: calc(100vh - 42px - 150px);
    margin: 10px;
    border-radius: 12px;
  }
}
</style>
