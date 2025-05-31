<template>
  <div class="tech-background-container">
    <!-- 粒子背景 -->
    <div class="particles-container" ref="particlesContainer"></div>
    
    <!-- 矩阵雨效果 -->
    <div class="matrix-rain-container" ref="matrixContainer"></div>
    
    <!-- 科技网格 -->
    <div class="tech-grid"></div>
    
    <!-- 能量线条 -->
    <div class="energy-lines">
      <div class="energy-line" v-for="i in 8" :key="i" :style="getLineStyle(i)"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const particlesContainer = ref<HTMLElement>()
const matrixContainer = ref<HTMLElement>()

let animationFrame: number
let particles: Array<{
  x: number
  y: number
  size: number
  speedX: number
  speedY: number
  opacity: number
  element: HTMLElement
}> = []

// 创建粒子
const createParticle = () => {
  if (!particlesContainer.value) return null

  const particle = document.createElement('div')
  particle.className = 'particle'
  
  const size = Math.random() * 4 + 1
  const x = Math.random() * window.innerWidth
  const y = window.innerHeight + 20
  const speedX = (Math.random() - 0.5) * 2
  const speedY = -(Math.random() * 3 + 1)
  const opacity = Math.random() * 0.8 + 0.2

  particle.style.width = `${size}px`
  particle.style.height = `${size}px`
  particle.style.left = `${x}px`
  particle.style.top = `${y}px`
  particle.style.opacity = `${opacity}`

  particlesContainer.value.appendChild(particle)

  return {
    x,
    y,
    size,
    speedX,
    speedY,
    opacity,
    element: particle
  }
}

// 更新粒子位置
const updateParticles = () => {
  particles = particles.filter(particle => {
    particle.x += particle.speedX
    particle.y += particle.speedY
    particle.opacity -= 0.01 // 加快粒子消失速度

    particle.element.style.left = `${particle.x}px`
    particle.element.style.top = `${particle.y}px`
    particle.element.style.opacity = `${particle.opacity}`

    // 移除超出屏幕或透明的粒子
    if (particle.y < -20 || particle.opacity <= 0 || particle.x < -20 || particle.x > window.innerWidth + 20) {
      particle.element.remove()
      return false
    }
    return true
  })

  // 减少粒子生成频率和数量
  if (Math.random() < 0.1 && particles.length < 20) {
    const newParticle = createParticle()
    if (newParticle) particles.push(newParticle)
  }

  animationFrame = requestAnimationFrame(updateParticles)
}

// 创建矩阵雨字符 - 降低频率
const createMatrixColumn = () => {
  if (!matrixContainer.value) return

  const column = document.createElement('div')
  column.className = 'matrix-column'
  
  // 减少字符数量
  const characters = '01010101アイウエオ'
  const columnText = Array.from({ length: 8 }, () => 
    characters[Math.floor(Math.random() * characters.length)]
  ).join('')
  
  column.textContent = columnText
  column.style.left = `${Math.random() * window.innerWidth}px`
  column.style.animationDuration = `${Math.random() * 8 + 6}s` // 减慢动画速度
  column.style.fontSize = `${Math.random() * 6 + 12}px`
  column.style.opacity = `${Math.random() * 0.6 + 0.2}` // 降低透明度

  matrixContainer.value.appendChild(column)

  // 动画结束后移除元素
  column.addEventListener('animationend', () => {
    column.remove()
  })
}

// 获取能量线条样式
const getLineStyle = (index: number) => {
  const angle = (index * 45) % 360
  const delay = index * 1 // 增加延迟
  return {
    transform: `rotate(${angle}deg)`,
    animationDelay: `${delay}s`
  }
}

onMounted(() => {
  // 开始粒子动画
  updateParticles()
  
  // 降低矩阵雨生成频率
  const matrixInterval = setInterval(() => {
    if (Math.random() < 0.05) { // 降低生成概率
      createMatrixColumn()
    }
  }, 1000) // 增加间隔时间

  // 清理函数
  onUnmounted(() => {
    if (animationFrame) {
      cancelAnimationFrame(animationFrame)
    }
    clearInterval(matrixInterval)
    particles.forEach(particle => particle.element.remove())
    particles = []
  })
})
</script>

<style scoped>
.tech-background-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: -10;
  overflow: hidden;
}

.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.matrix-rain-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 科技网格背景 - 性能优化 */
.tech-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(0, 212, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.05) 1px, transparent 1px);
  background-size: 100px 100px;
  animation: techGridMove 60s linear infinite; /* 减慢动画速度 */
  will-change: transform; /* 性能优化 */
}

@keyframes techGridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(100px, 100px);
  }
}

/* 能量线条 - 性能优化 */
.energy-lines {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
}

.energy-line {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px;
  height: 100px;
  background: linear-gradient(to bottom, var(--primary-color), transparent);
  transform-origin: 0 0;
  animation: energyLinePulse 6s ease-in-out infinite; /* 减慢动画 */
  will-change: opacity, height; /* 性能优化 */
}

@keyframes energyLinePulse {
  0%, 100% {
    opacity: 0.2; /* 降低最小透明度 */
    height: 50px;
  }
  50% {
    opacity: 0.8; /* 降低最大透明度 */
    height: 120px;
  }
}

/* 粒子样式 - 性能优化 */
:deep(.particle) {
  position: absolute;
  background: radial-gradient(circle, var(--primary-color), var(--accent-color));
  border-radius: 50%;
  pointer-events: none;
  will-change: transform, opacity; /* 性能优化 */
  /* 移除粒子发光动画以提升性能 */
}

/* 矩阵雨样式 - 性能优化 */
:deep(.matrix-column) {
  position: absolute;
  color: var(--primary-color);
  font-family: var(--font-mono);
  text-shadow: 0 0 3px var(--primary-color); /* 减少阴影模糊 */
  animation: matrixFall linear infinite;
  white-space: nowrap;
  writing-mode: vertical-rl;
  text-orientation: upright;
  will-change: transform, opacity; /* 性能优化 */
}

@keyframes matrixFall {
  0% {
    transform: translateY(-200px);
    opacity: 0;
  }
  10% {
    opacity: 0.6; /* 降低透明度 */
  }
  90% {
    opacity: 0.6;
  }
  100% {
    transform: translateY(100vh);
    opacity: 0;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .tech-grid {
    background-size: 50px 50px;
  }
  
  .energy-lines {
    width: 150px;
    height: 150px;
  }
  
  .energy-line {
    height: 75px;
  }
  
  @keyframes energyLinePulse {
    0%, 100% {
      opacity: 0.3;
      height: 30px;
    }
    50% {
      opacity: 1;
      height: 100px;
    }
  }
}

/* 减少动效模式 */
@media (prefers-reduced-motion: reduce) {
  .tech-grid,
  .energy-line,
  :deep(.particle),
  :deep(.matrix-column) {
    animation: none;
  }
  
  :deep(.particle) {
    opacity: 0.3;
  }
  
  :deep(.matrix-column) {
    display: none;
  }
}
</style> 