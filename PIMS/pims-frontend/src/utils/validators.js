// 表单验证器

// 手机号验证
export const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 房号验证 (格式: A101, B202等)
export const validateRoomNumber = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入房号'))
  } else if (!/^[A-Z]\d{3}$/.test(value)) {
    callback(new Error('房号格式不正确，例如：A101'))
  } else {
    callback()
  }
}

// 身份证号验证
export const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入身份证号'))
  } else if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/.test(value)) {
    callback(new Error('请输入正确的身份证号'))
  } else {
    callback()
  }
}

// 密码验证
export const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

// 验证码验证
export const validateVerifyCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('验证码为6位数字'))
  } else {
    callback()
  }
}

// 身份证号验证器（用于ApplyResident）
export const idCardValidator = validateIdCard
