<!-- 技术栈：Vue 3, Element Plus, Axios, Vue Router, TypeScript -->
<template>
  <div class="home-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
          v-model="keyword"
          placeholder="搜索商家或菜品"
          clearable
          @clear="fetchMerchants"
          @keyup.enter="fetchMerchants"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 排序栏 -->
    <div class="sort-bar">
      <el-radio-group v-model="sortType" size="small" @change="fetchMerchants">
        <el-radio-button value="distance">距离最近</el-radio-button>
        <el-radio-button value="rating">评分最高</el-radio-button>
        <el-radio-button value="sales">销量最高</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 商家列表 -->
    <div class="merchant-list" v-loading="loading">
      <div v-if="merchants.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无商家" />
      </div>
      <div
          v-for="item in merchants"
          :key="item.id"
          class="merchant-card"
          @click="goDetail(item.id)"
      >
        <el-image
            :src="item.image || defaultImg"
            class="merchant-img"
            fit="cover"
        />
        <div class="merchant-info">
          <div class="merchant-name">{{ item.name }}</div>
          <div class="merchant-meta">
            <span class="score">
              <el-icon><StarFilled /></el-icon> {{ item.score || 4.5 }}
            </span>
            <span class="sales">月销 {{ item.monthlySales || 0 }}</span>
            <span class="distance" v-if="item.distance">{{ item.distance }}km</span>
          </div>
          <div class="merchant-tags">
            <el-tag size="small" v-if="item.deliveryFee === 0" type="success">免配送费</el-tag>
            <el-tag size="small" v-else>配送 ¥{{ item.deliveryFee }}</el-tag>
            <el-tag size="small">起送 ¥{{ item.minOrderAmount }}</el-tag>
            <el-tag size="small" v-if="item.avgDeliveryTime">约{{ item.avgDeliveryTime }}分钟</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, StarFilled } from '@element-plus/icons-vue'
import { merchantApi } from '../../api/merchant'

const router = useRouter()
const keyword = ref('')
const sortType = ref('distance')
const loading = ref(false)
const merchants = ref<any[]>([])
const defaultImg = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

// 获取商家列表
const fetchMerchants = async () => {
  loading.value = true
  try {
    // TODO: 需后端补充 /user/merchant/list 接口，目前先模拟数据
    // const res = await merchantApi.getList(sortType.value, keyword.value)
    // merchants.value = res.data

    // 模拟数据，后续对接真实API
    merchants.value = [
      {
        id: 1,
        name: '肯德基宅急送',
        image: '',
        score: 4.7,
        monthlySales: 2893,
        deliveryFee: 0,
        minOrderAmount: 20,
        avgDeliveryTime: 30,
        distance: 1.2
      },
      {
        id: 2,
        name: '麦当劳麦乐送',
        image: '',
        score: 4.5,
        monthlySales: 3120,
        deliveryFee: 2.5,
        minOrderAmount: 15,
        avgDeliveryTime: 25,
        distance: 0.8
      },
      {
        id: 3,
        name: '本地小厨',
        image: '',
        score: 4.8,
        monthlySales: 567,
        deliveryFee: 3,
        minOrderAmount: 20,
        avgDeliveryTime: 35,
        distance: 2.0
      }
    ]
  } catch (error) {
    // 暂时忽略，真实接口时启用
  } finally {
    loading.value = false
  }
}

const goDetail = (id: number) => {
  router.push(`/merchant/${id}`)
}

onMounted(() => {
  fetchMerchants()
})
</script>

<style scoped>
.home-container {
  padding: 10px 16px 60px;
  background: #f5f5f5;
  min-height: 100vh;
}
.search-bar {
  margin-bottom: 12px;
}
.sort-bar {
  margin-bottom: 16px;
}
.merchant-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}
.merchant-img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  flex-shrink: 0;
  margin-right: 12px;
}
.merchant-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.merchant-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.merchant-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  gap: 12px;
}
.score {
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 2px;
}
.merchant-tags {
  display: flex;
  gap: 6px;
}
.empty-tip {
  padding-top: 60px;
}
</style>