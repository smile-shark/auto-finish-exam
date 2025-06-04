import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/form/HomeFormView.vue'
import ShowContentView from '../views/form/ShowContentView.vue'
import LoginView from '../views/form/LoginFormView.vue'
import ToolSelectQuestionsView from '../views/form/Tool/ToolSelectQuestionsView.vue'
import ToolFinishQuestionsView from '../views/form/Tool/ToolFinishQuestionsView.vue'
import CompleteDailySummaryView from '../views/form/Tool/CompleteDailySummaryView.vue'
import ToolTeacherFinishTestExam from '@/views/form/Tool/ToolTeacherFinishTestExam.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import Home from '@/views/admin/page/Home.vue'
import AdminCourseList from '@/views/admin/page/AdminCourseList.vue'
import AdminUserList from '@/views/admin/page/AdminUserList.vue'


Vue.use(VueRouter)

const routes = [

  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/main',
    name: 'name',
    component: HomeView,
    children: [
      {
        path: '',
        name: 'home',
        component: ShowContentView
      },
      {
        path: '/SelectTool',
        name: 'selectTool',
        component: ToolSelectQuestionsView
      },
      {
        path: '/FinishTool',
        name: 'finishTool',
        component: ToolFinishQuestionsView
      }, {
        path: '/CompleteDaily',
        name: 'completeDaily',
        component: CompleteDailySummaryView
      }, {
        path: '/TeacherFinishTestExam',
        name: 'teacherFinishTestExam',
        component: ToolTeacherFinishTestExam
      }
    ]
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminHome,
    children: [
      {
        path: '',
        name: 'adminHome',
        component: Home
      },
      {
        path: '/admin/courseList',
        name: 'adminCourseList',
        component: AdminCourseList
      },
      {
        path: '/admin/userList',
        name: 'adminUserList',
        component: AdminUserList
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
