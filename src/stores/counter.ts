import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useShareStore = defineStore('sharedData', () => {
  // 使用 ref 创建一个响应式的布尔值，初始值为 false
  const isCollapse = ref(false);
  const HotCoverImgSrc = ref("")

  // 定义一个方法用于切换 isCollapse 的值
  function changeCollapse() {
    // 直接对 isCollapse.value 取反
    isCollapse.value = !isCollapse.value;
  }
  // 定义一个方法用于传入 HotCoverImgSrc 的值
  function postSrc(src:string) {
    // 直接对 isCollapse.value 取反
    HotCoverImgSrc.value = src
  }
  // 返回需要暴露的状态和方法
  return { isCollapse,postSrc,HotCoverImgSrc, changeCollapse };
});
