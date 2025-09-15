<template>
  <div class="home-container dark-theme">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-content">
        <div class="logo">
          <div class="logo-icon">
            <div class="arrow-left"></div>
            <div class="arrow-right"></div>
          </div>
          <span class="logo-text">Algorithm Navigation</span>
        </div>
        
        
        <div class="theme-toggle" @click="toggleTheme">
          <span class="theme-icon sun" :class="{ active: !isDarkMode }">☀️</span>
          <span class="theme-icon moon" :class="{ active: isDarkMode }">🌙</span>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="content-wrapper">
        <!-- 居中内容 -->
        <div class="center-content">
          <h1 class="main-title">
            {{ titleText }}<span class="cursor" v-if="isTyping || titleText.length < fullTitle.length">|</span>
          </h1>
          <p class="main-description">
            {{ descriptionText }}<span class="cursor" v-if="isTyping && titleText.length >= fullTitle.length">|</span>
          </p>

          <div class="action-buttons">
            <button class="btn btn-primary" @click="navigateTo('/love-master')">
              AI Algorithm Master
            </button>
            <button class="btn btn-secondary" @click="navigateTo('/super-agent')">
              AI Super Agent
            </button>
          </div>
        </div>
      </div>
      
      <!-- 动态背景元素 -->
      <div class="dynamic-background">
        <!-- 浮动粒子 -->
        <div class="floating-particles">
          <div class="particle particle-1"></div>
          <div class="particle particle-2"></div>
          <div class="particle particle-3"></div>
          <div class="particle particle-4"></div>
          <div class="particle particle-5"></div>
          <div class="particle particle-6"></div>
          <div class="particle particle-7"></div>
          <div class="particle particle-8"></div>
        </div>
        
        <!-- 几何形状 -->
        <div class="geometric-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
        </div>
        
        <!-- 代码雨 -->
        <div class="code-rain">
          <div class="code-line" v-for="i in 20" :key="i" :style="{ animationDelay: (i * 0.1) + 's' }">
            {{ getRandomCode() }}
          </div>
        </div>
        
        <!-- 新增：动态网格 -->
        <div class="animated-grid">
          <div class="grid-line" v-for="i in 12" :key="'grid-' + i" :style="{ animationDelay: (i * 0.2) + 's' }"></div>
        </div>
        
        <!-- 新增：脉冲光环 -->
        <div class="pulse-rings">
          <div class="pulse-ring ring-1"></div>
          <div class="pulse-ring ring-2"></div>
          <div class="pulse-ring ring-3"></div>
        </div>
        
        <!-- 新增：动态光线 -->
        <div class="light-rays">
          <div class="light-ray ray-1"></div>
          <div class="light-ray ray-2"></div>
          <div class="light-ray ray-3"></div>
          <div class="light-ray ray-4"></div>
        </div>
      </div>
    </main>
    
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import AppFooter from '../components/AppFooter.vue'

// Set page title and metadata
useHead({
  title: 'EK AI Intelligent Agent Platform - Home',
  meta: [
    {
      name: 'description',
      content: 'EK AI Intelligent Agent Platform provides services such as the AI Algorithm Master and AI Super Agent, meeting your diverse needs for AI-powered conversations, intelligent problem-solving, and professional guidance.'
    },
    {
      name: 'keywords',
      content: 'AI Agent, AI Applications, AI Algorithm Master, AI Assistant, Intelligent Conversations, EK, AI Super Agent, Home'
    }
  ]
})

const router = useRouter()

// Theme state management
const isDarkMode = ref(true) // Default to dark mode

// Typing effect state
const titleText = ref('')
const descriptionText = ref('')
const isTyping = ref(true)

// Original text
const fullTitle = 'AI Algorithm Super Agent'
const fullDescription = 'Your all-in-one intelligent companion for algorithms, advanced AI interactions, and problem-solving.'


const navigateTo = (path) => {
  router.push(path)
}

// 主题切换功能
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  updateTheme()
}

// 更新主题样式
const updateTheme = () => {
  const homeContainer = document.querySelector('.home-container')
  if (homeContainer) {
    if (isDarkMode.value) {
      homeContainer.classList.add('dark-theme')
      homeContainer.classList.remove('light-theme')
    } else {
      homeContainer.classList.add('light-theme')
      homeContainer.classList.remove('dark-theme')
    }
  }
}

// 打字机效果函数
const typeWriter = async (text, targetRef, speed = 100) => {
  for (let i = 0; i <= text.length; i++) {
    targetRef.value = text.slice(0, i)
    await new Promise(resolve => setTimeout(resolve, speed))
  }
}

// 启动打字机效果
const startTypewriterEffect = async () => {
  // 先打标题
  await typeWriter(fullTitle, titleText, 150)
  // 等待一下再打描述
  await new Promise(resolve => setTimeout(resolve, 500))
  await typeWriter(fullDescription, descriptionText, 80)
  isTyping.value = false
}

// 初始化主题和打字机效果
onMounted(async () => {
  // 默认设置为暗色主题
  isDarkMode.value = true
  updateTheme()
  
  // 启动打字机效果
  await nextTick()
  startTypewriterEffect()
})

// 生成随机代码字符串
const getRandomCode = () => {
  const codeSnippets = [
    'function bubbleSort(arr) {',
    'const binarySearch = (arr, target) => {',
    'class TreeNode {',
    'def quickSort(arr):',
    'public void mergeSort(int[] arr) {',
    'const graph = new Map();',
    'function dfs(node, visited) {',
    'for (let i = 0; i < n; i++) {',
    'if (condition) return true;',
    'const dp = new Array(n).fill(0);',
    'while (left <= right) {',
    'const hash = new Map();',
    'function backtrack(path, choices) {',
    'const memo = new Map();',
    'return Math.max(...arr);',
    'const stack = [];',
    'const queue = [];',
    'const visited = new Set();',
    'const result = [];',
    'const temp = arr[i];',
    'arr[i] = arr[j];',
    'arr[j] = temp;'
  ]
  return codeSnippets[Math.floor(Math.random() * codeSnippets.length)]
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

/* 全局样式变量 */
:root {
  --primary-blue: #4A90E2;
  --secondary-blue: #357ABD;
  --light-blue: #E3F2FD;
  --dark-blue: #1A365D;
  --white: #FFFFFF;
  --gray-100: #F7FAFC;
  --gray-200: #EDF2F7;
  --gray-300: #E2E8F0;
  --gray-600: #718096;
  --gray-800: #2D3748;
  --green: #48BB78;
  --orange: #ED8936;
  --red: #F56565;
}

.home-container {
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

/* 顶部导航栏样式 */
.top-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  display: flex;
  align-items: center;
  gap: 4px;
}

.arrow-left, .arrow-right {
  width: 0;
  height: 0;
  border-style: solid;
}

.arrow-left {
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 12px solid var(--white);
}

.arrow-right {
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 12px solid var(--white);
}

.logo-text {
  font-family: 'Inter', sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--white);
}


.theme-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 8px 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.theme-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.theme-icon {
  font-size: 1.2rem;
  transition: all 0.3s ease;
  opacity: 0.4;
  filter: grayscale(100%);
}

.theme-icon.active {
  opacity: 1;
  filter: grayscale(0%);
  transform: scale(1.2);
}

.theme-icon:hover {
  opacity: 0.8;
  filter: grayscale(50%);
}

/* 主要内容区域样式 */
.main-content {
  flex: 1;
  padding: 60px 20px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 140px);
}

.content-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  position: relative;
  z-index: 10;
}

/* 居中内容样式 */
.center-content {
  text-align: center;
  max-width: 800px;
  position: relative;
  z-index: 10;
}

.main-title {
  font-family: 'Inter', sans-serif;
  font-size: 3.5rem;
  font-weight: 700;
  color: #4fc3f7;
  line-height: 1.2;
  margin-bottom: 30px;
  text-shadow: 
    0 0 8px #4fc3f7,
    0 0 16px #4fc3f7,
    0 0 24px #4fc3f7,
    0 4px 20px rgba(0, 0, 0, 0.5);
  animation: titleGlow 2s ease-in-out infinite alternate;
}

/* 光标样式 */
.cursor {
  color: #4fc3f7;
  animation: blink 1s infinite;
  font-weight: normal;
}

/* 标题发光动画 */
@keyframes titleGlow {
  0% {
    text-shadow: 
      0 0 8px #4fc3f7,
      0 0 16px #4fc3f7,
      0 0 24px #4fc3f7,
      0 4px 20px rgba(0, 0, 0, 0.5);
  }
  100% {
    text-shadow: 
      0 0 12px #4fc3f7,
      0 0 20px #4fc3f7,
      0 0 28px #4fc3f7,
      0 4px 20px rgba(0, 0, 0, 0.5);
  }
}

/* 光标闪烁动画 */
@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

.main-description {
  font-family: 'Inter', sans-serif;
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin-bottom: 50px;
  max-width: 450px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.btn {
  font-family: 'Inter', sans-serif;
  font-size: 1.1rem;
  font-weight: 600;
  padding: 16px 32px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  background: #00e5ff;
  color: #0a0a0a;
  border: 2px solid #00e5ff;
  box-shadow: 0 0 15px rgba(0, 229, 255, 0.5);
}

.btn-primary:hover {
  background: transparent;
  color: #00e5ff;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 229, 255, 0.6);
}

.btn-secondary {
  background: transparent;
  color: #81d4fa;
  border: 2px solid #81d4fa;
  box-shadow: 0 0 10px rgba(129, 212, 250, 0.3);
}

.btn-secondary:hover {
  background: #81d4fa;
  color: #0a0a0a;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(129, 212, 250, 0.5);
}

/* 动态背景效果 */
.dynamic-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

/* 浮动粒子效果 */
.floating-particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 8s infinite ease-in-out;
}

.particle-1 {
  width: 4px;
  height: 4px;
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.particle-2 {
  width: 6px;
  height: 6px;
  top: 30%;
  left: 80%;
  animation-delay: 1s;
}

.particle-3 {
  width: 3px;
  height: 3px;
  top: 60%;
  left: 10%;
  animation-delay: 2s;
}

.particle-4 {
  width: 5px;
  height: 5px;
  top: 70%;
  left: 90%;
  animation-delay: 3s;
}

.particle-5 {
  width: 4px;
  height: 4px;
  top: 40%;
  left: 50%;
  animation-delay: 4s;
}

.particle-6 {
  width: 7px;
  height: 7px;
  top: 80%;
  left: 30%;
  animation-delay: 5s;
}

.particle-7 {
  width: 3px;
  height: 3px;
  top: 10%;
  left: 70%;
  animation-delay: 6s;
}

.particle-8 {
  width: 5px;
  height: 5px;
  top: 50%;
  left: 60%;
  animation-delay: 7s;
}

/* 几何形状效果 */
.geometric-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: rotate 15s infinite linear;
}

.shape-1 {
  width: 60px;
  height: 60px;
  top: 15%;
  right: 20%;
  border-radius: 50%;
  animation-delay: 0s;
}

.shape-2 {
  width: 40px;
  height: 40px;
  top: 25%;
  left: 15%;
  transform: rotate(45deg);
  animation-delay: 3s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  right: 15%;
  border-radius: 50%;
  animation-delay: 6s;
}

.shape-4 {
  width: 50px;
  height: 50px;
  bottom: 30%;
  left: 25%;
  transform: rotate(45deg);
  animation-delay: 9s;
}

.shape-5 {
  width: 30px;
  height: 30px;
  top: 60%;
  left: 80%;
  border-radius: 50%;
  animation-delay: 12s;
}

/* 代码雨效果 */
.code-rain {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  opacity: 0.1;
}

.code-line {
  position: absolute;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  white-space: nowrap;
  animation: rain 10s infinite linear;
  left: 0;
  top: -50px;
}

/* 动画定义 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px) translateX(0px);
    opacity: 0.3;
  }
  25% {
    transform: translateY(-20px) translateX(10px);
    opacity: 0.8;
  }
  50% {
    transform: translateY(-10px) translateX(-10px);
    opacity: 0.5;
  }
  75% {
    transform: translateY(-30px) translateX(5px);
    opacity: 0.9;
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes rain {
  0% {
    transform: translateY(-100vh);
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  90% {
    opacity: 0.6;
  }
  100% {
    transform: translateY(100vh);
    opacity: 0;
  }
}

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

@media (max-width: 1024px) {
  .main-content {
    min-height: calc(100vh - 120px);
  }
  
  .center-content {
    max-width: 90%;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 15px;
    height: 60px;
  }
  
  
  .main-content {
    padding: 40px 15px;
    min-height: calc(100vh - 100px);
  }
  
  .main-title {
    font-size: 2.5rem;
    text-shadow: 
      0 0 6px #4fc3f7,
      0 0 12px #4fc3f7,
      0 0 18px #4fc3f7,
      0 2px 15px rgba(0, 0, 0, 0.5);
  }
  
  .main-description {
    font-size: 1.1rem;
    margin-bottom: 40px;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .btn {
    font-size: 1rem;
    padding: 14px 28px;
  }
  
  /* 移动端减少动画效果 */
  .particle {
    animation-duration: 12s;
  }
  
  .shape {
    animation-duration: 20s;
  }
  
  .code-line {
    animation-duration: 15s;
  }
  
  /* 移动端简化新增效果 */
  .animated-grid {
    opacity: 0.3;
  }
  
  .pulse-ring {
    animation-duration: 8s;
    opacity: 0.6;
  }
  
  .light-ray {
    animation-duration: 12s;
    opacity: 0.4;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 10px;
    height: 50px;
  }
  
  .logo-text {
    font-size: 1.2rem;
  }
  
  .main-content {
    padding: 30px 10px;
    min-height: calc(100vh - 80px);
  }
  
  .main-title {
    font-size: 2rem;
    text-shadow: 
      0 0 4px #4fc3f7,
      0 0 8px #4fc3f7,
      0 0 12px #4fc3f7,
      0 2px 10px rgba(0, 0, 0, 0.5);
  }
  
  .main-description {
    font-size: 1rem;
  }
  
  .btn {
    font-size: 0.9rem;
    padding: 12px 24px;
  }
  
  /* 小屏幕进一步减少动画 */
  .particle {
    animation-duration: 15s;
    opacity: 0.5;
  }
  
  .shape {
    animation-duration: 25s;
    opacity: 0.3;
  }
  
  .code-rain {
    opacity: 0.05;
  }
  
  /* 小屏幕隐藏部分复杂效果 */
  .animated-grid {
    display: none;
  }
  
  .pulse-ring {
    animation-duration: 10s;
    opacity: 0.3;
  }
  
  .light-ray {
    display: none;
  }
}

/* 亮色主题样式 */
.home-container.light-theme {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
    url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 60 60"><circle cx="30" cy="30" r="1" fill="%23ffffff" opacity="0.3"/></svg>');
}

.home-container.light-theme .top-header {
  background: rgba(255, 255, 255, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.4);
}

.home-container.light-theme .logo-text {
  color: var(--white);
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.home-container.light-theme .main-title {
  color: #1976d2 !important;
  text-shadow: 
    0 0 8px #1976d2,
    0 0 16px #1976d2,
    0 2px 8px rgba(0, 0, 0, 0.3) !important;
  animation: titleGlowLight 2s ease-in-out infinite alternate !important;
}

.home-container.light-theme .cursor {
  color: #1976d2 !important;
  animation: blink 1s infinite !important;
}

/* 亮色主题标题发光动画 */
@keyframes titleGlowLight {
  0% {
    text-shadow: 
      0 0 8px #1976d2,
      0 0 16px #1976d2,
      0 2px 8px rgba(0, 0, 0, 0.3);
  }
  100% {
    text-shadow: 
      0 0 12px #1976d2,
      0 0 20px #1976d2,
      0 2px 8px rgba(0, 0, 0, 0.3);
  }
}

.home-container.light-theme .main-description {
  color: rgba(255, 255, 255, 0.95);
}

.home-container.light-theme .btn-primary {
  background: #00bcd4 !important;
  color: #ffffff !important;
  border: 2px solid #00bcd4 !important;
  box-shadow: 0 0 15px rgba(0, 188, 212, 0.6) !important;
}

.home-container.light-theme .btn-primary:hover {
  background: transparent !important;
  color: #00bcd4 !important;
  box-shadow: 0 8px 25px rgba(0, 188, 212, 0.7) !important;
}

.home-container.light-theme .btn-secondary {
  background: transparent !important;
  color: #4dd0e1 !important;
  border: 2px solid #4dd0e1 !important;
  box-shadow: 0 0 10px rgba(77, 208, 225, 0.4) !important;
}

.home-container.light-theme .btn-secondary:hover {
  background: #4dd0e1 !important;
  color: #ffffff !important;
  box-shadow: 0 8px 25px rgba(77, 208, 225, 0.6) !important;
}

.home-container.light-theme .particle {
  background: rgba(255, 255, 255, 0.2);
}

.home-container.light-theme .shape {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.home-container.light-theme .code-line {
  color: rgba(255, 255, 255, 0.7);
}

/* 暗色主题样式 */
.home-container.dark-theme {
  background: #0a0a0a !important;
  background-image: 
    radial-gradient(circle at 20% 20%, #1a237e 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, #0d47a1 0%, transparent 50%),
    radial-gradient(circle at 40% 60%, #1565c0 0%, transparent 50%),
    linear-gradient(45deg, #0a0a0a 0%, #1a1a2e 25%, #16213e 50%, #0f3460 75%, #0a0a0a 100%) !important;
  background-size: 100% 100%, 100% 100%, 100% 100%, 200% 200% !important;
  background-position: 0% 0%, 100% 100%, 50% 50%, 0% 0% !important;
  animation: backgroundShift 20s ease-in-out infinite !important;
}

.home-container.dark-theme .top-header {
  background: rgba(13, 71, 161, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
}

.home-container.dark-theme .logo-text {
  color: #ffffff !important;
  text-shadow: 
    0 0 8px rgba(255, 255, 255, 0.8),
    0 0 16px rgba(255, 255, 255, 0.6),
    0 2px 4px rgba(0, 0, 0, 0.3) !important;
}

.home-container.dark-theme .main-title {
  color: #4fc3f7 !important;
  text-shadow: 
    0 0 8px #4fc3f7,
    0 0 16px #4fc3f7,
    0 0 24px #4fc3f7,
    0 4px 20px rgba(0, 0, 0, 0.5) !important;
  animation: titleGlow 2s ease-in-out infinite alternate !important;
}

.home-container.dark-theme .cursor {
  color: #4fc3f7 !important;
  animation: blink 1s infinite !important;
}

.home-container.dark-theme .main-description {
  color: rgba(255, 255, 255, 0.95);
}

.home-container.dark-theme .btn-primary {
  background: #00e5ff !important;
  color: #0a0a0a !important;
  border: 2px solid #00e5ff !important;
  box-shadow: 0 0 15px rgba(0, 229, 255, 0.5) !important;
}

.home-container.dark-theme .btn-primary:hover {
  background: transparent !important;
  color: #00e5ff !important;
  box-shadow: 0 8px 25px rgba(0, 229, 255, 0.6) !important;
}

.home-container.dark-theme .btn-secondary {
  background: transparent !important;
  color: #81d4fa !important;
  border: 2px solid #81d4fa !important;
  box-shadow: 0 0 10px rgba(129, 212, 250, 0.3) !important;
}

.home-container.dark-theme .btn-secondary:hover {
  background: #81d4fa !important;
  color: #0a0a0a !important;
  box-shadow: 0 8px 25px rgba(129, 212, 250, 0.5) !important;
}

.home-container.dark-theme .particle {
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.1);
}

.home-container.dark-theme .shape {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.05);
}

.home-container.dark-theme .code-line {
  color: rgba(255, 255, 255, 0.7);
  text-shadow: 0 0 3px rgba(255, 255, 255, 0.2);
}

/* 新增：动态网格样式 */
.animated-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.grid-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(74, 144, 226, 0.3), transparent);
  height: 1px;
  width: 100%;
  animation: gridPulse 4s infinite ease-in-out;
}

.grid-line:nth-child(odd) {
  top: 20%;
  animation-delay: 0s;
}

.grid-line:nth-child(even) {
  top: 80%;
  animation-delay: 2s;
}

/* 新增：脉冲光环样式 */
.pulse-rings {
  position: absolute;
  width: 100%;
  height: 100%;
}

.pulse-ring {
  position: absolute;
  border: 2px solid rgba(74, 144, 226, 0.4);
  border-radius: 50%;
  animation: pulse 6s infinite ease-in-out;
}

.ring-1 {
  width: 200px;
  height: 200px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.ring-2 {
  width: 300px;
  height: 300px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.ring-3 {
  width: 150px;
  height: 150px;
  bottom: 30%;
  left: 70%;
  animation-delay: 4s;
}

/* 新增：动态光线样式 */
.light-rays {
  position: absolute;
  width: 100%;
  height: 100%;
}

.light-ray {
  position: absolute;
  background: linear-gradient(45deg, transparent, rgba(74, 144, 226, 0.6), transparent);
  width: 2px;
  height: 100%;
  animation: lightSweep 8s infinite ease-in-out;
  transform-origin: top;
}

.ray-1 {
  left: 25%;
  animation-delay: 0s;
}

.ray-2 {
  left: 50%;
  animation-delay: 2s;
}

.ray-3 {
  left: 75%;
  animation-delay: 4s;
}

.ray-4 {
  left: 90%;
  animation-delay: 6s;
}

/* 新增动画关键帧 */
@keyframes gridPulse {
  0%, 100% {
    opacity: 0;
    transform: scaleX(0);
  }
  50% {
    opacity: 1;
    transform: scaleX(1);
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.5);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

@keyframes lightSweep {
  0% {
    transform: rotate(0deg);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: rotate(180deg);
    opacity: 0;
  }
}
</style> 