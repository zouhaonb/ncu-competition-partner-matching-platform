<!-- App.vue - 根组件 -->
<template>
  <div id="app-container">
    <NavBar />
    <main class="main-content">
      <router-view />
    </main>
    <footer class="app-footer">
      <span>南昌大学竞赛队友匹配平台 &copy; 2026 - 让组队更简单</span>
    </footer>
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style scoped>
#app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
}

.app-footer {
  text-align: center;
  padding: 16px;
  color: #999;
  font-size: 13px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}
</style>
