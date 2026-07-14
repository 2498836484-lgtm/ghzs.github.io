# PIMS 后端API测试脚本

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  PIMS 后端功能测试脚本" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api"

# 测试计数器
$testTotal = 0
$testPassed = 0
$testFailed = 0

function Test-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    $script:testTotal++
    Write-Host "[$script:testTotal] 测试: $Name" -ForegroundColor Yellow
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            UseBasicParsing = $true
            ContentType = "application/json"
        }
        
        if ($Headers.Count -gt 0) {
            $params.Headers = $Headers
        }
        
        if ($Body -ne $null) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-WebRequest @params
        
        if ($response.StatusCode -eq 200) {
            $script:testPassed++
            Write-Host "   ✅ 成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
            
            # 解析响应
            $result = $response.Content | ConvertFrom-Json
            if ($result.code -eq 200) {
                Write-Host "   📝 返回码: $($result.code) - $($result.message)" -ForegroundColor Gray
                return $result
            } else {
                Write-Host "   ⚠️  业务错误码: $($result.code) - $($result.message)" -ForegroundColor Yellow
                return $result
            }
        } else {
            $script:testFailed++
            Write-Host "   ❌ 失败 (状态码: $($response.StatusCode))" -ForegroundColor Red
            return $null
        }
    }
    catch {
        $script:testFailed++
        Write-Host "   ❌ 异常: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
    finally {
        Write-Host ""
    }
}

# ===== 1. 管理员登录获取Token =====
Write-Host "`n========== 第一步: 管理员登录 ==========" -ForegroundColor Cyan
$loginBody = @{
    username = "admin"
    password = "admin123"
}
$adminLoginResult = Test-Api -Name "管理员登录" -Method POST `
    -Url "$baseUrl/admin/login" -Body $loginBody

if ($adminLoginResult -and $adminLoginResult.data) {
    $adminToken = $adminLoginResult.data.token
    Write-Host "🔑 管理员Token: $($adminToken.Substring(0, 30))..." -ForegroundColor Green
} else {
    Write-Host "❌ 管理员登录失败，无法继续测试" -ForegroundColor Red
    exit
}

# ===== 2. 用户注册和登录 =====
Write-Host "`n========== 第二步: 用户注册登录 ==========" -ForegroundColor Cyan

# 发送验证码
$sendCodeResult = Test-Api -Name "发送验证码" -Method POST `
    -Url "$baseUrl/user/send-code?phone=13800138888"

# 用户注册
$registerBody = @{
    phone = "13800138888"
    password = "123456"
    roomNumber = "A201"
    verifyCode = "123456"
}
$registerResult = Test-Api -Name "用户注册" -Method POST `
    -Url "$baseUrl/user/register" -Body $registerBody

# 用户登录
$userLoginBody = @{
    username = "A201"
    password = "123456"
}
$userLoginResult = Test-Api -Name "用户登录（房号）" -Method POST `
    -Url "$baseUrl/user/login/room" -Body $userLoginBody

if ($userLoginResult -and $userLoginResult.data) {
    $userToken = $userLoginResult.data.token
    $userId = $userLoginResult.data.userId
    Write-Host "🔑 用户Token: $($userToken.Substring(0, 30))..." -ForegroundColor Green
    Write-Host "👤 用户ID: $userId" -ForegroundColor Green
} else {
    Write-Host "❌ 用户登录失败，使用默认Token继续测试" -ForegroundColor Yellow
}

# ===== 3. 入住申请和审核 =====
Write-Host "`n========== 第三步: 入住申请流程 ==========" -ForegroundColor Cyan

# 提交入住申请
$applyBody = @{
    name = "张三"
    idCard = "110101199001011234"
    phone = "13800138888"
    area = 100.00
    pricePerSqm = 3.50
}
$applyResult = Test-Api -Name "提交入住申请" -Method POST `
    -Url "$baseUrl/user/apply-resident?userId=$userId" -Body $applyBody

# 查询审核状态
$auditStatusResult = Test-Api -Name "查询审核状态" -Method GET `
    -Url "$baseUrl/user/audit-status?userId=$userId"

# 管理员查询待审核列表
$pendingListResult = Test-Api -Name "获取待审核列表" -Method GET `
    -Url "$baseUrl/admin/audit/pending-list?pageNum=1&pageSize=10"

# 管理员审核通过
if ($pendingListResult -and $pendingListResult.data -and $pendingListResult.data.records.Count -gt 0) {
    $auditId = $pendingListResult.data.records[0].id
    Write-Host "📋 审核记录ID: $auditId" -ForegroundColor Green
    
    $auditBody = @{
        auditId = $auditId
        status = 1
        auditRemark = "审核通过"
    }
    $processAuditResult = Test-Api -Name "管理员审核通过" -Method POST `
        -Url "$baseUrl/admin/audit/process?adminId=1" -Body $auditBody
}

# ===== 4. 缴费管理测试（新增功能） =====
Write-Host "`n========== 第四步: 缴费管理功能 ==========" -ForegroundColor Cyan

if ($userToken) {
    # 提交缴费记录
    $paymentBody = @{
        paymentMonth = "2024-12"
        amount = 350.00
        roomNumber = "A201"
        area = 100.00
        pricePerSqm = 3.50
    }
    $submitPaymentResult = Test-Api -Name "提交缴费记录" -Method POST `
        -Url "$baseUrl/payment/submit" -Body $paymentBody `
        -Headers @{ Authorization = "Bearer $userToken" }
    
    # 查询个人缴费记录
    $paymentListResult = Test-Api -Name "查询个人缴费记录" -Method GET `
        -Url "$baseUrl/payment/list?pageNum=1&pageSize=10" `
        -Headers @{ Authorization = "Bearer $userToken" }
    
    # 管理员查询所有缴费记录
    $allPaymentListResult = Test-Api -Name "管理员查询所有缴费记录" -Method GET `
        -Url "$baseUrl/admin/payment/list?pageNum=1&pageSize=10"
    
    if ($paymentListResult -and $paymentListResult.data -and $paymentListResult.data.records.Count -gt 0) {
        $paymentId = $paymentListResult.data.records[0].id
        Write-Host "💰 缴费记录ID: $paymentId" -ForegroundColor Green
        
        # 查询缴费详情
        $paymentDetailResult = Test-Api -Name "查询缴费详情" -Method GET `
            -Url "$baseUrl/payment/detail/$paymentId"
    }
}

# ===== 5. 消息管理测试（新增功能） =====
Write-Host "`n========== 第五步: 消息管理功能 ==========" -ForegroundColor Cyan

if ($userToken) {
    # 获取未读消息数量
    $unreadCountResult = Test-Api -Name "获取未读消息数量" -Method GET `
        -Url "$baseUrl/message/unread/count" `
        -Headers @{ Authorization = "Bearer $userToken" }
    
    if ($unreadCountResult -and $unreadCountResult.data) {
        Write-Host "📧 未读消息数: $($unreadCountResult.data)" -ForegroundColor Green
    }
    
    # 获取消息列表
    $messageListResult = Test-Api -Name "获取消息列表" -Method GET `
        -Url "$baseUrl/message/list?pageNum=1&pageSize=10" `
        -Headers @{ Authorization = "Bearer $userToken" }
    
    if ($messageListResult -and $messageListResult.data -and $messageListResult.data.records.Count -gt 0) {
        $messageId = $messageListResult.data.records[0].id
        Write-Host "📨 消息ID: $messageId" -ForegroundColor Green
        
        # 标记消息为已读
        $markReadResult = Test-Api -Name "标记消息为已读" -Method POST `
            -Url "$baseUrl/message/read/$messageId" `
            -Headers @{ Authorization = "Bearer $userToken" }
    }
    
    # 标记所有消息为已读
    $markAllReadResult = Test-Api -Name "标记所有消息为已读" -Method POST `
        -Url "$baseUrl/message/read/all" `
        -Headers @{ Authorization = "Bearer $userToken" }
}

# ===== 6. 住户管理测试（新增功能） =====
Write-Host "`n========== 第六步: 住户管理功能 ==========" -ForegroundColor Cyan

# 查询住户列表
$residentListResult = Test-Api -Name "查询住户列表" -Method GET `
    -Url "$baseUrl/resident/list?pageNum=1&pageSize=10"

if ($residentListResult -and $residentListResult.data -and $residentListResult.data.records.Count -gt 0) {
    $residentId = $residentListResult.data.records[0].id
    Write-Host "🏠 住户ID: $residentId" -ForegroundColor Green
    
    # 查询住户详情
    $residentDetailResult = Test-Api -Name "查询住户详情" -Method GET `
        -Url "$baseUrl/resident/detail/$residentId"
}

# 根据用户ID查询住户
if ($userId) {
    $residentByUserResult = Test-Api -Name "根据用户ID查询住户" -Method GET `
        -Url "$baseUrl/resident/by-user/$userId"
}

# ===== 测试总结 =====
Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "          测试结果统计" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "总测试数: $testTotal" -ForegroundColor White
Write-Host "通过数: $testPassed" -ForegroundColor Green
Write-Host "失败数: $testFailed" -ForegroundColor Red
Write-Host "通过率: $([math]::Round($testPassed/$testTotal*100, 2))%" -ForegroundColor $(if ($testFailed -eq 0) { "Green" } else { "Yellow" })
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

if ($testFailed -eq 0) {
    Write-Host "✅ 所有测试通过！系统运行正常！" -ForegroundColor Green
} else {
    Write-Host "⚠️  有 $testFailed 个测试失败，请检查日志" -ForegroundColor Yellow
}

Write-Host "`n💡 提示：" -ForegroundColor Cyan
Write-Host "   - Swagger文档地址: http://localhost:8080/api/doc.html" -ForegroundColor Gray
Write-Host "   - 项目已启动在: http://localhost:8080/api" -ForegroundColor Gray
Write-Host ""
