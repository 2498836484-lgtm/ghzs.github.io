// Token管理
const TOKEN_KEY = 'pims_token'
const USER_TYPE_KEY = 'pims_user_type'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  return localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_TYPE_KEY)
}

export function getUserType() {
  return localStorage.getItem(USER_TYPE_KEY)
}

export function setUserType(type) {
  return localStorage.setItem(USER_TYPE_KEY, type)
}
