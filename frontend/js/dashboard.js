// dashboard.js — BookSphere Dashboard

async function loadDashboard() {
  const r = await apiGet('/api/dashboard');
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Failed to load stats', 'error');
    return;
  }
  const d = r.data.data;
  document.getElementById('stat-total').textContent    = d.totalBooks ?? 0;
  document.getElementById('stat-avail').textContent    = d.availableBooks ?? 0;
  document.getElementById('stat-issued').textContent   = d.issuedBooks ?? 0;
  document.getElementById('stat-students').textContent = d.totalStudents ?? 0;
}

async function loadRecentIssues() {
  const tbody = document.getElementById('recent-tbody');
  tbody.innerHTML = '<tr><td colspan="6" class="loading">Loading records...</td></tr>';
  const r = await apiGet('/api/issues');
  if (!r.ok || !r.data.success) {
    tbody.innerHTML = '<tr><td colspan="6" class="empty-state"><h3>Unable to load records</h3><p>' + escapeHtml(r.data.message || 'Error') + '</p></td></tr>';
    return;
  }
  const issues = r.data.data || [];
  const recent = issues.slice(0, 5);
  if (recent.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state"><div class="empty-state-icon">📋</div><h3>No circulation records yet</h3></div></td></tr>';
    return;
  }
  tbody.innerHTML = recent.map(i => `
    <tr>
      <td><strong>${escapeHtml(i.bookTitle)}</strong></td>
      <td>${escapeHtml(i.studentName)}</td>
      <td><span class="font-mono text-xs">${escapeHtml(i.rollNo)}</span></td>
      <td>${escapeHtml(i.issueDate)}</td>
      <td>${escapeHtml(i.dueDate)}</td>
      <td><span class="badge badge-${i.status.toLowerCase()}">${escapeHtml(i.status)}</span></td>
    </tr>`).join('');
}

async function loadPopularBooks() {
  const tbody = document.getElementById('popular-books-tbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="4" class="loading">Loading catalog...</td></tr>';
  
  const r = await apiGet('/api/books');
  if (!r.ok || !r.data.success) {
    tbody.innerHTML = '<tr><td colspan="4" class="empty-state"><h3>Unable to load catalog</h3></td></tr>';
    return;
  }
  const books = r.data.data || [];
  const topBooks = books.slice(0, 5);
  if (topBooks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="4"><div class="empty-state"><div class="empty-state-icon">📚</div><h3>No books added yet</h3></div></td></tr>';
    return;
  }
  tbody.innerHTML = topBooks.map(b => `
    <tr>
      <td>
        <div style="font-weight:800; color:#000;">${escapeHtml(b.title)}</div>
        <div style="font-size:11px; color:#4B5563;">by ${escapeHtml(b.author)}</div>
      </td>
      <td><span class="badge badge-${b.type.toLowerCase()}">${escapeHtml(b.type)}</span></td>
      <td><span class="font-mono text-xs font-bold">${b.availableCopies}/${b.totalCopies}</span></td>
      <td>
        ${b.availableCopies > 0
          ? `<span class="badge badge-available">In Stock</span>`
          : `<span class="badge badge-unavail">Out</span>`}
      </td>
    </tr>`).join('');
}

document.addEventListener('DOMContentLoaded', () => {
  loadDashboard();
  loadRecentIssues();
  loadPopularBooks();
});
