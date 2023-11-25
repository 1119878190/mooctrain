import { createStore } from 'vuex'

const MEMBER = "MEMBER"
export default createStore({
  state: {
    // 从sessionStorage中获取用户信息
    member: window.SessionStorage.get(MEMBER) || {}
  },
  getters: {
  },
  mutations: {
    // set值
    setMember(state,_member){
      state.member = _member;
      // 将用户信息保存到sessionStorage中
      window.SessionStorage.set(MEMBER, _member);
    }
  },
  actions: {
  },
  modules: {
  }
})
