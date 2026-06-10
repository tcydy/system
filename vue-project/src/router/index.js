import { createRouter, createWebHistory } from 'vue-router'
import { projectName } from "../../config/config.default.js";

const routes = [
  //通用路由
  {
    path: '/',
    name: '/',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录'
    }
  },

  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录'
    }
  },

  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: {
      title: '注册'
    }
  },

  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
  //下面都是前台路由
  {
    path: '/front',
    name: '/Front',
    component: () => import('../views/Front.vue'),
    //前台子路由
    children: [
      {
        path: 'home',
        name: 'FrontHome',
        component: () => import('../views/front/Home.vue'),
        mata: {
          title: '前台首页'
        }
      },

      {
        path: 'password',
        name: 'FrontPassword',
        component: () => import('../views/front/Password.vue'),
        meta: {
          title: '修改密码'
        }
      },
      {
        path: 'person',
        name: 'FrontPerson',
        component: () => import('../views/front/Person.vue'),
        meta: {
          title: '个人信息'
        }
      },
      {
        path: 'publish',
        name: 'FrontPublish',
        component: () => import('../views/front/Publish.vue'),
        meta: {
          title: '发布'
        }
      },
      {
        path: 'message',
        name: 'FrontMessage',
        component: () => import('../views/front/Message.vue'),
        meta: {
          title: '消息'
        }
      },
      {
        path: 'orders',
        name: 'FrontOrders',
        component: () => import('../views/front/Orders.vue'),
        meta: {
          title: '订单'
        }
      },
      {
        path: 'collect',
        name: 'FrontCollect',
        component: () => import('../views/front/Collect.vue'),
        meta: {
          title: '收藏'
        }
      },
      {
        path: 'user',
        name: 'FrontUser',
        component: () => import('../views/front/User.vue'),
        meta: {
          title: '我的'
        }
      },
      {
        path: 'search',
        name: 'FrontSearch',
        component: () => import('../views/front/Search.vue'),
        meta: {
          title: '搜索'
        }
      },
      {
        path: 'goodsDetail',
        name: 'FrontGoodsDetail',
        component: () => import('../views/front/GoodsDetail.vue'),
        meta: {
          title: '商品详情'
        }
      },
      {
        path: 'confirm',
        name: 'FrontConfirm',
        component: () => import('../views/front/Confirm.vue'),
        meta: {
          title: '下单确认'
        }
      },
      {
        path: 'address',
        name: 'FrontAddress',
        component: () => import('../views/front/Address.vue'),
        meta: {
          title: '收货地址'
        }
      },
    ]
  },
  //下面都是后台路由
  {
    path: '/back',
    name: 'back',
    component: () => import('../views/Back.vue'),
    //后台子路由
    children: [
      {
        path: 'home',
        name: 'BackHome',
        component: () => import('../views/back/Home.vue'),
        meta: {
          title: '后台首页'
        }
      },
      {
        path: 'password',
        name: 'BackPassword',
        component: () => import('../views/back/Password.vue'),
        meta: {
          title: '修改密码'
        }
      },
      {
        path: 'person',
        name: 'BackPerson',
        component: () => import('../views/back/Person.vue'),
        meta: {
          title: '个人信息'
        }
      },
      {
        path: 'user',
        name: 'BackUser',
        component: () => import('../views/back/User.vue'),
        meta: {
          title: '用户管理'
        }
      },
      {
        path: 'admin',
        name: 'BackAdmin',
        component: () => import('../views/back/Admin.vue'),
        meta: {
          title: '管理员管理'
        }
      },
      {
        path: 'notice',
        name: 'BackNotice',
        component: () => import('../views/back/Notice.vue'),
        meta: {
          title: '公告管理'
        }
      },
      {
        path: 'banner',
        name: 'BackBanner',
        component: () => import('../views/back/Banner.vue'),
        meta: {
          title: '轮播图管理'
        }
      },
      {
        path: 'type',
        name: 'BackType',
        component: () => import('../views/back/Type.vue'),
        meta: {
          title: '分类管理'
        }
      },
      {
        path: 'goods',
        name: 'BackGoods',
        component: () => import('../views/back/Goods.vue'),
        meta: {
          title: '闲置物品管理'
        }
      },
      {
        path: 'address',
        name: 'BackAddress',
        component: () => import('../views/back/Address.vue'),
        meta: {
          title: '收货地址管理'
        }
      },

    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})


//全局前置守卫
router.beforeEach((to, from, next) => {
  if (to.matched.length === 0) {
    next('/404')
    return
  }
  const accountstr = localStorage.getItem('account')
  const account = accountstr ? JSON.parse(accountstr) : null
  if (to.path === '/') {
    if (account && account.role) {
      if (account.role === 'ROLE_ADMIN') {
        next('/back/home')
      } else {
        next('/front/home')
      }
    }
    else {
      next('/login')
    }
  }
  else {
    next()
  }
})

//全局后置守卫
router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title}-${projectName}` : projectName//设置页面标题
})

export default router
