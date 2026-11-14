// common.js — Shared utilities for BookSphere

const SIDEBAR_STORAGE_KEY = 'booksphere_sidebar_collapsed';

// Check saved sidebar preference from localStorage
function isSidebarCollapsedSaved() {
  try {
    return localStorage.getItem(SIDEBAR_STORAGE_KEY) === 'true';
  } catch (e) {
    return false;
  }
}

// Apply collapsed state class to DOM
function applySidebarCollapsed(collapsed) {
  if (collapsed) {
    document.body.classList.add('sidebar-collapsed');
    document.documentElement.classList.add('sidebar-collapsed');
  } else {
    document.body.classList.remove('sidebar-collapsed');
    document.documentElement.classList.remove('sidebar-collapsed');
  }

  const btn = document.querySelector('.sidebar-toggle-btn');
  if (btn) {
    btn.setAttribute('aria-expanded', String(!collapsed));
    btn.setAttribute('title', collapsed ? 'Expand Sidebar' : 'Collapse Sidebar');
  }
}

// Immediately apply saved state before paint if on desktop
if (typeof window !== 'undefined' && window.innerWidth > 900 && isSidebarCollapsedSaved()) {
  document.documentElement.classList.add('sidebar-collapsed');
}

// Mark active navigation item based on current URL
function setActiveNav() {
  const path = window.location.pathname;
  const navLinks = document.querySelectorAll('.nav-item a');
  
  navLinks.forEach(a => {
    a.classList.remove('active');
    const href = a.getAttribute('href');
    if (href) {
      if (
        path.endsWith(href) ||
        ((path === '/' || path === '' || path.endsWith('/dashboard')) && href === 'dashboard.html')
      ) {
        a.classList.add('active');
      }
    }
  });
}

// Toggle sidebar functionality (Desktop collapsible + Mobile off-canvas drawer)
function toggleSidebar(forceState) {
  const isDesktop = window.innerWidth > 900;

  if (isDesktop) {
    const currentlyCollapsed = document.body.classList.contains('sidebar-collapsed');
    const shouldCollapse = (typeof forceState === 'boolean') ? forceState : !currentlyCollapsed;
    
    applySidebarCollapsed(shouldCollapse);
    try {
      localStorage.setItem(SIDEBAR_STORAGE_KEY, String(shouldCollapse));
    } catch (e) {
      // Storage unavailable
    }
  } else {
    // Mobile / Tablet Drawer behavior
    const sidebar = document.querySelector('.sidebar');
    let backdrop = document.querySelector('.sidebar-backdrop');
    
    if (!backdrop) {
      backdrop = document.createElement('div');
      backdrop.className = 'sidebar-backdrop';
      document.body.appendChild(backdrop);
      backdrop.addEventListener('click', () => {
        if (sidebar) sidebar.classList.remove('mobile-open');
        backdrop.classList.remove('active');
      });
    }

    if (sidebar) {
      const isOpen = (typeof forceState === 'boolean') ? forceState : !sidebar.classList.contains('mobile-open');
      sidebar.classList.toggle('mobile-open', isOpen);
      backdrop.classList.toggle('active', isOpen);
    }
  }
}

// Window resize listener to keep layout clean across screen breakpoints
window.addEventListener('resize', () => {
  const isDesktop = window.innerWidth > 900;
  const sidebar = document.querySelector('.sidebar');
  const backdrop = document.querySelector('.sidebar-backdrop');

  if (isDesktop) {
    if (sidebar) sidebar.classList.remove('mobile-open');
    if (backdrop) backdrop.classList.remove('active');
    applySidebarCollapsed(isSidebarCollapsedSaved());
  } else {
    document.body.classList.remove('sidebar-collapsed');
    document.documentElement.classList.remove('sidebar-collapsed');
  }
});

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
  toast.innerHTML = `<span style="font-size:16px;">${type === 'success' ? '✓' : '✗'}</span><span>${escapeHtml(message)}</span>`;
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

// Close modal on backdrop click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.add('hidden');
  }
});

// Run on page load
document.addEventListener('DOMContentLoaded', () => {
  setActiveNav();
  
  // Apply saved desktop sidebar state on load
  if (window.innerWidth > 900) {
    applySidebarCollapsed(isSidebarCollapsedSaved());
  }

  // Attach click listener to any toggle button on the page
  const toggleButtons = document.querySelectorAll('.sidebar-toggle-btn, .mobile-menu-btn');
  toggleButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      toggleSidebar();
    });
  });
});
