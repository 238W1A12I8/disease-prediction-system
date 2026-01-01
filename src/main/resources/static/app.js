// ========================================
// Disease Prediction System - App Logic
// Industry Grade v4.0
// ========================================

(function() {
    // ========== TOKEN MANAGEMENT ==========
    function getToken() {
        return localStorage.getItem('token');
    }

    function setToken(token) {
        localStorage.setItem('token', token);
    }

    function clearToken() {
        localStorage.removeItem('token');
    }

    // ========== JWT PARSING ==========
    function parseToken(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            return JSON.parse(window.atob(base64));
        } catch (e) {
            return null;
        }
    }

    function parseEmailFromToken() {
        const token = getToken();
        if (!token) return null;
        const payload = parseToken(token);
        return payload ? payload.sub : null;
    }

    function parseRoleFromToken() {
        const token = getToken();
        if (!token) return null;
        const payload = parseToken(token);
        return payload ? payload.role : null;
    }

    function getInitials(email) {
        if (!email) return 'U';
        const name = email.split('@')[0];
        return name.charAt(0).toUpperCase();
    }

    // ========== THEME MANAGEMENT ==========
    function getTheme() {
        return localStorage.getItem('theme') || 'light';
    }

    function setTheme(theme) {
        localStorage.setItem('theme', theme);
        document.documentElement.setAttribute('data-theme', theme);
        updateThemeIcons(theme);
    }

    function toggleTheme() {
        const current = getTheme();
        const newTheme = current === 'light' ? 'dark' : 'light';
        setTheme(newTheme);
        return newTheme;
    }

    function initTheme() {
        const saved = getTheme();
        document.documentElement.setAttribute('data-theme', saved);
        updateThemeIcons(saved);
    }

    function updateThemeIcons(theme) {
        document.querySelectorAll('.icon-sun').forEach(el => {
            el.style.display = theme === 'dark' ? 'block' : 'none';
        });
        document.querySelectorAll('.icon-moon').forEach(el => {
            el.style.display = theme === 'light' ? 'block' : 'none';
        });
    }

    // ========== API REQUEST HELPER ==========
    async function request(url, options = {}) {
        const token = getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
            ...options.headers
        };
        
        try {
            const response = await fetch(url, { ...options, headers });
            
            if (response.status === 401) {
                clearToken();
                window.location.href = '/auth.html';
                return null;
            }
            
            const text = await response.text();
            let data;
            try {
                data = text ? JSON.parse(text) : {};
            } catch {
                data = { message: text };
            }
            
            if (!response.ok) {
                throw new Error(data.message || data.error || 'Request failed');
            }
            
            return data;
        } catch (error) {
            if (error.message === 'Failed to fetch') {
                throw new Error('Network error. Please check if the server is running.');
            }
            throw error;
        }
    }

    // ========== TOAST NOTIFICATIONS ==========
    let toastContainer = null;

    function ensureToastContainer() {
        if (!toastContainer || !document.body.contains(toastContainer)) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container';
            document.body.appendChild(toastContainer);
        }
        return toastContainer;
    }

    function showToast(message, type = 'info', duration = 4000) {
        const container = ensureToastContainer();
        
        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };
        
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span class="toast-icon">${icons[type] || icons.info}</span>
            <span class="toast-message">${message}</span>
            <button class="toast-close" onclick="this.parentElement.classList.add('removing'); setTimeout(() => this.parentElement.remove(), 300);">×</button>
        `;
        
        container.appendChild(toast);
        
        // Auto remove
        setTimeout(() => {
            if (toast.parentElement) {
                toast.classList.add('removing');
                setTimeout(() => toast.remove(), 300);
            }
        }, duration);
    }

    // ========== SKELETON LOADERS ==========
    function showSkeleton(container, count = 3, type = 'card') {
        const skeletons = {
            card: '<div class="skeleton skeleton-card"></div>',
            text: '<div class="skeleton skeleton-title"></div><div class="skeleton skeleton-text"></div><div class="skeleton skeleton-text" style="width:80%"></div>',
            stat: '<div class="stat-card"><div class="skeleton" style="width:56px;height:56px;border-radius:12px"></div><div style="flex:1"><div class="skeleton skeleton-text" style="width:60%"></div><div class="skeleton" style="height:2rem;width:80px"></div></div></div>'
        };
        
        container.innerHTML = Array(count).fill(skeletons[type] || skeletons.card).join('');
    }

    function hideSkeleton(container) {
        container.querySelectorAll('.skeleton, .skeleton-card').forEach(el => el.remove());
    }

    // ========== ANIMATED COUNTERS ==========
    function animateCounter(element, target, duration = 1500) {
        const start = 0;
        const startTime = performance.now();
        
        function update(currentTime) {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            // Easing function (ease-out)
            const eased = 1 - Math.pow(1 - progress, 3);
            const current = Math.floor(start + (target - start) * eased);
            
            element.textContent = current.toLocaleString();
            
            if (progress < 1) {
                requestAnimationFrame(update);
            } else {
                element.textContent = target.toLocaleString();
            }
        }
        
        requestAnimationFrame(update);
    }

    // ========== LOADING OVERLAY ==========
    let loadingOverlay = null;

    function showLoading(message = 'Loading...') {
        if (!loadingOverlay) {
            loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'loading-overlay';
            loadingOverlay.innerHTML = `
                <div style="text-align:center;color:white">
                    <div class="spinner spinner-lg" style="margin:0 auto 16px;border-color:rgba(255,255,255,0.3);border-top-color:white"></div>
                    <div class="loading-text">${message}</div>
                </div>
            `;
            document.body.appendChild(loadingOverlay);
        }
        loadingOverlay.querySelector('.loading-text').textContent = message;
        setTimeout(() => loadingOverlay.classList.add('show'), 10);
    }

    function hideLoading() {
        if (loadingOverlay) {
            loadingOverlay.classList.remove('show');
        }
    }

    // ========== DROPDOWN TOGGLE ==========
    function initDropdowns() {
        document.querySelectorAll('.dropdown').forEach(dropdown => {
            const trigger = dropdown.querySelector('.dropdown-trigger, .profile-trigger');
            if (trigger) {
                trigger.addEventListener('click', (e) => {
                    e.stopPropagation();
                    // Close other dropdowns
                    document.querySelectorAll('.dropdown.open').forEach(d => {
                        if (d !== dropdown) d.classList.remove('open');
                    });
                    dropdown.classList.toggle('open');
                });
            }
        });
        
        // Close on outside click
        document.addEventListener('click', () => {
            document.querySelectorAll('.dropdown.open').forEach(d => d.classList.remove('open'));
        });
    }

    // ========== SIDEBAR TOGGLE ==========
    function toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        const hamburger = document.querySelector('.hamburger-btn');
        
        if (sidebar && overlay) {
            sidebar.classList.toggle('open');
            overlay.classList.toggle('show');
            if (hamburger) hamburger.classList.toggle('active');
        }
    }

    // ========== PDF DOWNLOAD ==========
    async function downloadPredictionPDF(predictionId) {
        try {
            showLoading('Generating PDF...');
            const token = getToken();
            const response = await fetch(`/api/predictions/${predictionId}/pdf`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (!response.ok) throw new Error('Failed to generate PDF');
            
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `prediction-${predictionId}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
            
            showToast('PDF downloaded successfully', 'success');
        } catch (error) {
            showToast('Failed to download PDF: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    // ========== SYMPTOM SEARCH ==========
    function initSymptomSearch(inputId, listId, symptoms) {
        const input = document.getElementById(inputId);
        const list = document.getElementById(listId);
        if (!input || !list) return;

        const suggestionsDiv = document.createElement('div');
        suggestionsDiv.className = 'search-suggestions';
        input.parentElement.appendChild(suggestionsDiv);

        input.addEventListener('input', () => {
            const query = input.value.toLowerCase().trim();
            if (query.length < 2) {
                suggestionsDiv.classList.remove('show');
                return;
            }

            const matches = symptoms.filter(s => 
                s.name.toLowerCase().includes(query) && 
                !Array.from(list.selectedOptions).some(o => o.value == s.id)
            ).slice(0, 8);

            if (matches.length === 0) {
                suggestionsDiv.classList.remove('show');
                return;
            }

            suggestionsDiv.innerHTML = matches.map(s => 
                `<div class="suggestion-item" data-id="${s.id}" data-name="${s.name}">${s.name}</div>`
            ).join('');
            suggestionsDiv.classList.add('show');
        });

        suggestionsDiv.addEventListener('click', (e) => {
            if (e.target.classList.contains('suggestion-item')) {
                const id = e.target.dataset.id;
                const option = list.querySelector(`option[value="${id}"]`);
                if (option) option.selected = true;
                input.value = '';
                suggestionsDiv.classList.remove('show');
                showToast(`Added: ${e.target.dataset.name}`, 'success');
            }
        });

        document.addEventListener('click', (e) => {
            if (!input.contains(e.target) && !suggestionsDiv.contains(e.target)) {
                suggestionsDiv.classList.remove('show');
            }
        });
    }

    // ========== RISK LEVEL HELPER ==========
    function getRiskLevel(confidence) {
        if (confidence >= 80) return { level: 'high', label: 'High Risk', class: 'risk-high' };
        if (confidence >= 50) return { level: 'medium', label: 'Medium Risk', class: 'risk-medium' };
        return { level: 'low', label: 'Low Risk', class: 'risk-low' };
    }

    // ========== DATE FORMATTING ==========
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatRelativeTime(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 7) return formatDate(dateString);
        if (days > 0) return `${days} day${days > 1 ? 's' : ''} ago`;
        if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
        if (minutes > 0) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
        return 'Just now';
    }

    // ========== LOGOUT ==========
    function logout() {
        clearToken();
        showToast('Logged out successfully', 'success');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 500);
    }

    // ========== NAVBAR SCROLL EFFECT ==========
    function initNavbarScroll() {
        const navbar = document.querySelector('.hero-navbar');
        if (!navbar) return;

        window.addEventListener('scroll', () => {
            if (window.scrollY > 20) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }

    // ========== INITIALIZATION ==========
    document.addEventListener('DOMContentLoaded', () => {
        initTheme();
        initDropdowns();
        initNavbarScroll();
    });

    // ========== EXPORT API ==========
    window.appApi = {
        // Token
        getToken,
        setToken,
        clearToken,
        parseEmailFromToken,
        parseRoleFromToken,
        getInitials,
        // Theme
        getTheme,
        setTheme,
        toggleTheme,
        initTheme,
        // Request
        request,
        // Toast
        showToast,
        // Loading
        showLoading,
        hideLoading,
        showSkeleton,
        hideSkeleton,
        // Animation
        animateCounter,
        // Utilities
        toggleSidebar,
        initDropdowns,
        initSymptomSearch,
        getRiskLevel,
        formatDate,
        formatRelativeTime,
        downloadPredictionPDF,
        logout
    };
})();
