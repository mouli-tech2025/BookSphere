// dashboard.js

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
  tbody.innerHTML = '<tr><td colspan="6" class="loading">Loading...</td></tr>';
  const r = await apiGet('/api/issues');
  if (!r.ok || !r.data.success) {
    tbody.innerHTML = '<tr><td colspan="6" class="empty-state"><h3>Unable to load records</h3><p>' + escapeHtml(r.data.message || 'Error') + '</p></td></tr>';
    return;
  }
  const issues = r.data.data || [];
  const recent = issues.slice(0, 5);
  if (recent.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state"><div class="empty-state-icon">📋</div><h3>No issue records yet</h3></div></td></tr>';
    return;
  }
  tbody.innerHTML = recent.map(i => `
    <tr>
      <td><strong>${escapeHtml(i.bookTitle)}</strong></td>
      <td>${escapeHtml(i.studentName)}</td>
      <td>${escapeHtml(i.rollNo)}</td>
      <td>${escapeHtml(i.issueDate)}</td>
      <td>${escapeHtml(i.dueDate)}</td>
      <td><span class="badge badge-${i.status.toLowerCase()}">${escapeHtml(i.status)}</span></td>
    </tr>`).join('');
}

document.addEventListener('DOMContentLoaded', () => {
  loadDashboard();
  loadRecentIssues();
});
