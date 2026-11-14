// common.js — Shared utilities: navbar active state, toast, modal, escapeHtml

// Mark the current page's nav link as active
function setActiveNav() {
  const path = window.location.pathname;
  document.querySelectorAll('.navbar-links a').forEach(a => {
    a.classList.remove('active');
    const href = a.getAttribute('href');
    if (href && (path.endsWith(href) || (path === '/' && href === 'dashboard.html'))) {
      a.classList.add('active');
    }
  });
}

// Escape HTML to prevent injection
function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// Toast notifications
let toastContainer;
function getToastContainer() {
  if (!toastContainer) {
    toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
      toastContainer = document.createElement('div');
      toastContainer.className = 'toast-container';
      document.body.appendChild(toastContainer);
    }
  }
  return toastContainer;
}

function showToast(message, type = 'success') {
  const container = getToastContainer();
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `<span>${type === 'success' ? '✓' : '✗'}</span><span>${escapeHtml(message)}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s ease';
  }, 2700);
  setTimeout(() => toast.remove(), 3000);
}

// Generic modal helpers
function openModal(overlayId) {
  const el = document.getElementById(overlayId);
  if (el) el.classList.remove('hidden');
}

function closeModal(overlayId) {
  const el = document.getElementById(overlayId);
  if (el) el.classList.add('hidden');
}

// Close modal on overlay click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.add('hidden');
  }
});

// Run on every page
document.addEventListener('DOMContentLoaded', setActiveNav);
