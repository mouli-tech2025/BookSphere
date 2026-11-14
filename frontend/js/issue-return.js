// issue-return.js

async function populateDropdowns() {
  // Populate student dropdown
  const sr = await apiGet('/api/students');
  const studSel = document.getElementById('f-student');
  studSel.innerHTML = '<option value="">Select Student...</option>';
  if (sr.ok && sr.data.success && sr.data.data) {
    sr.data.data.forEach(s => {
      studSel.innerHTML += `<option value="${s.id}">${escapeHtml(s.name)} (${escapeHtml(s.rollNo)})</option>`;
    });
  }

  // Populate book dropdown (only books with availableCopies > 0)
  const br = await apiGet('/api/books');
  const bookSel = document.getElementById('f-book');
  bookSel.innerHTML = '<option value="">Select Book...</option>';
  if (br.ok && br.data.success && br.data.data) {
    br.data.data.filter(b => b.availableCopies > 0).forEach(b => {
      bookSel.innerHTML += `<option value="${b.id}">${escapeHtml(b.title)} [${b.availableCopies} available - ${escapeHtml(b.type)}]</option>`;
    });
  }
}

async function checkAvailability() {
  const bookId = document.getElementById('f-book').value;
  const hint   = document.getElementById('avail-hint');
  if (!bookId) {
    hint.style.display = 'none';
    return;
  }

  const r = await apiGet('/api/books/' + bookId + '/availability');
  hint.style.display = 'block';
  if (r.ok && r.data.success && r.data.data) {
    const avail = r.data.data.available;
    const copies = r.data.data.availableCopies;
    hint.className = 'availability-hint ' + (avail ? 'hint-available' : 'hint-unavailable');
    hint.textContent = avail
      ? `✓ Available: ${copies} cop${copies === 1 ? 'y' : 'ies'} ready to issue`
      : '✗ Out of Stock: 0 copies available';
  } else {
    hint.style.display = 'none';
  }
}

async function issueBook(e) {
  e.preventDefault();
  const studentId = document.getElementById('f-student').value;
  const bookId    = document.getElementById('f-book').value;

  if (!studentId || !bookId) {
    showToast('Please select both a student and a book.', 'error');
    return;
  }

  const r = await apiPost('/api/issue', { studentId, bookId });
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Book issue failed', 'error');
    return;
  }

  const rec = r.data.data;
  showToast(`Book issued successfully! Due date: ${rec.dueDate}`, 'success');
  document.getElementById('issue-form').reset();
  document.getElementById('avail-hint').style.display = 'none';
  populateDropdowns();
  loadIssues();
}

async function returnBook(issueId) {
  if (!confirm('Confirm return for this book?')) return;
  const r = await apiPost('/api/return', { issueId });
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Book return failed', 'error');
    return;
  }
  showToast(r.data.message || 'Book returned successfully.', 'success');
  populateDropdowns();
  loadIssues();
}

async function loadIssues() {
  const status = document.getElementById('status-filter').value;
  const tbody  = document.getElementById('issues-tbody');
  tbody.innerHTML = '<tr><td colspan="8" class="loading">Loading...</td></tr>';

  const params = new URLSearchParams();
  if (status) params.set('status', status);
  const r = await apiGet('/api/issues?' + params.toString());
  if (!r.ok || !r.data.success) {
    tbody.innerHTML = '<tr><td colspan="8"><div class="empty-state"><h3>Unable to load issue records</h3></div></td></tr>';
    return;
  }

  const issues = r.data.data || [];
  if (issues.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8"><div class="empty-state">
      <div class="empty-state-icon">📋</div><h3>No issue records found</h3></div></td></tr>`;
    return;
  }
  tbody.innerHTML = issues.map(i => `
    <tr>
      <td><strong>${escapeHtml(i.bookTitle)}</strong></td>
      <td>${escapeHtml(i.studentName)}</td>
      <td>${escapeHtml(i.rollNo)}</td>
      <td>${escapeHtml(i.issueDate)}</td>
      <td>${escapeHtml(i.dueDate)}</td>
      <td>${i.returnDate ? escapeHtml(i.returnDate) : '—'}</td>
      <td>${i.fine > 0 ? `<strong style="color:var(--coral)">₹${Number(i.fine).toFixed(2)}</strong>` : '₹0.00'}</td>
      <td>
        <div style="display:flex;gap:6px;align-items:center;">
          <span class="badge badge-${i.status.toLowerCase()}">${escapeHtml(i.status)}</span>
          ${i.status === 'ISSUED' ? `<button class="btn btn-success btn-sm" onclick="returnBook(${i.issueId})">Return</button>` : ''}
        </div>
      </td>
    </tr>`).join('');
}

document.addEventListener('DOMContentLoaded', () => {
  populateDropdowns();
  loadIssues();
  document.getElementById('status-filter').addEventListener('change', loadIssues);
  document.getElementById('issue-form').addEventListener('submit', issueBook);
  document.getElementById('f-book').addEventListener('change', checkAvailability);
});
