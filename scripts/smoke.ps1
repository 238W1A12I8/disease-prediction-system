param(
    [string]$BaseUrl = "http://localhost:8082",
    [string]$Email = "user@demo.com",
    [string]$Password = "password",
    [string[]]$Symptoms = @("fever","cough")
)

$ErrorActionPreference = 'Stop'

Write-Host "[smoke] Hitting $BaseUrl" -ForegroundColor Cyan

function Invoke-JsonPost($uri, $body, $headers = @{}) {
    $json = $body | ConvertTo-Json -Depth 4
    return Invoke-RestMethod -Method Post -Uri $uri -ContentType 'application/json' -Headers $headers -Body $json
}

# Health
$health = Invoke-RestMethod -Method Get -Uri "$BaseUrl/actuator/health"
Write-Host "[health] status: $($health.status)" -ForegroundColor Green

# Login
$loginResp = Invoke-JsonPost "$BaseUrl/auth/login" @{ email = $Email; password = $Password }
if (-not $loginResp.token) { throw "Login failed" }
$token = $loginResp.token
Write-Host "[login] token acquired" -ForegroundColor Green

# Predict
$predictResp = Invoke-JsonPost "$BaseUrl/predict" @{ symptoms = $Symptoms } @{ Authorization = "Bearer $token" }
Write-Host "[predict] disease: $($predictResp.diseaseName), confidence: $($predictResp.confidence)" -ForegroundColor Green
