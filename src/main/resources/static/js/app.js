const API = '';   // ← FIXED: was 'http://localhost:8080'

// ── Token helpers ────────────────────────────────────────────────────
// NOTE: JWT is also stored server-side in HttpSession by AuthProxyController.
//       sessionStorage here is used only for displaying username/role in the UI.
const Auth = {
    getToken:    () => sessionStorage.getItem('jwt_token'),
    getUsername: () => sessionStorage.getItem('username'),
    getRole:     () => sessionStorage.getItem('role'),
    set(token, username, role) {
        sessionStorage.setItem('jwt_token', token);
        sessionStorage.setItem('username',  username);
        sessionStorage.setItem('role',      role);
    },
    clear() {
        sessionStorage.removeItem('jwt_token');
        sessionStorage.removeItem('username');
        sessionStorage.removeItem('role');
    },
    // User is "logged in" if they have a session — the server checks this.
    // For client-side UI decisions, check sessionStorage or the hidden meta tag.
    isLoggedIn: () => {
        const meta = document.querySelector('meta[name="auth-user"]');
        return meta ? true : !!sessionStorage.getItem('jwt_token');
    }
};

// On page load, hydrate sessionStorage from Thymeleaf meta tags
// so username/role display works even after a page refresh.
document.addEventListener('DOMContentLoaded', () => {
    const metaUser  = document.querySelector('meta[name="auth-user"]');
    const metaRole  = document.querySelector('meta[name="auth-role"]');
    const metaToken = document.querySelector('meta[name="auth-token"]');

    if (metaUser && !Auth.getUsername()) {
        Auth.set(
            metaToken?.content || '',
            metaUser.content   || '',
            metaRole?.content  || ''
        );
    }

    const usernameEl = document.getElementById('nav-username');
    const roleEl     = document.getElementById('nav-role');
    const avatarEl   = document.getElementById('nav-avatar');

    if (usernameEl) usernameEl.textContent = Auth.getUsername() || 'User';
    if (roleEl)     roleEl.textContent     = Auth.getRole()?.replace('ROLE_', '') || '';
    if (avatarEl)   avatarEl.textContent   = (Auth.getUsername() || 'U')[0].toUpperCase();
});

// ── API fetch wrapper ─────────────────────────────────────────────────
// Calls are now made to the SAME ORIGIN (/api/v1/...)
// The Spring frontend's ApiProxyController.java forwards them to the backend.
async function apiFetch(endpoint, options = {}) {
    // Include credentials so the session cookie is sent
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    try {
        const res = await fetch(`${API}${endpoint}`, {
            ...options,
            headers,
            credentials: 'same-origin'   // sends session cookie automatically
        });

        if (res.status === 401) {
            Auth.clear();
            window.location.href = '/login';
            return null;
        }

        if (res.status === 403) {
            showToast('You do not have permission for this action.', 'error');
            return null;
        }

        // Handle non-JSON gracefully (e.g. unexpected HTML error pages)
        const contentType = res.headers.get('content-type') || '';
        if (!contentType.includes('application/json')) {
            if (!res.ok) {
                showToast(`Server error (${res.status}). Is the backend running?`, 'error');
                return null;
            }
        }

        const json = await res.json();

        if (!res.ok) {
            showToast(json.message || `Error ${res.status}`, 'error');
            return null;
        }

        return json;

    } catch (err) {
        showToast('Cannot connect to server. Is the backend running on port 8080?', 'error');
        console.error('[apiFetch]', endpoint, err);
        return null;
    }
}

// ── Toast notifications ───────────────────────────────────────────────
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const icon  = type === 'success' ? '✓' : '✕';
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => toast.remove(), 3500);
}

// ── Loading state helpers ─────────────────────────────────────────────
function setLoading(tbodyId, colSpan = 6) {
    const tbody = document.getElementById(tbodyId);
    if (tbody) {
        tbody.innerHTML = `
            <tr class="loading-row">
                <td colspan="${colSpan}">
                    <span class="spinner"></span> Loading...
                </td>
            </tr>`;
    }
}

function setEmpty(tbodyId, message = 'No records found', colSpan = 6) {
    const tbody = document.getElementById(tbodyId);
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${colSpan}">
                    <div class="empty-state">
                        <div class="empty-icon">📭</div>
                        <p>${message}</p>
                    </div>
                </td>
            </tr>`;
    }
}

// ── Status badge helper ───────────────────────────────────────────────
function statusBadge(status) {
    if (!status) return '-';
    const map = {
        'Delivered':  'success',
        'Pending':    'warning',
        'Cancelled':  'danger',
        'Active':     'success',
        'Inactive':   'danger',
        'ROLE_ADMIN': 'orange',
        'ROLE_USER':  'info'
    };
    const cls = map[status] || 'info';
    return `<span class="badge badge-${cls}">${status}</span>`;
}

// ── Pagination renderer ───────────────────────────────────────────────
function renderPagination(containerId, page, totalPages, onPageChange) {
    const el = document.getElementById(containerId);
    if (!el) return;

    const prevDisabled = page <= 0 ? 'disabled' : '';
    const nextDisabled = page >= totalPages - 1 ? 'disabled' : '';

    el.innerHTML = `
        <span class="pagination-info">
            Page ${page + 1} of ${totalPages}
        </span>
        <div class="pagination-btns">
            <button class="page-btn" ${prevDisabled}
                onclick="(${onPageChange})(${page - 1})">← Prev</button>
            <button class="page-btn active">${page + 1}</button>
            <button class="page-btn" ${nextDisabled}
                onclick="(${onPageChange})(${page + 1})">Next →</button>
        </div>`;
}

// ── Stat counter animation ────────────────────────────────────────────
function animateCount(el, target) {
    if (!el) return;
    let current = 0;
    const step  = Math.max(1, Math.ceil(target / 30));
    const timer = setInterval(() => {
        current += step;
        if (current >= target) {
            current = target;
            clearInterval(timer);
        }
        el.textContent = current;
    }, 30);
}

// Logout ────────────────────────────────────────────────────────────
function logout() {
    Auth.clear();
    fetch('/logout', { method: 'POST', credentials: 'same-origin' })
        .finally(() => { window.location.href = '/login'; });
}