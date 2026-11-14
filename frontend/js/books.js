// books.js — BookSphere Books Management

let editingBookId = null;

async function loadBooks() {
  const q        = document.getElementById('search-query').value.trim();
  const category = document.getElementById('search-category').value;
  const tbody    = document.getElementById('books-tbody');
  tbody.innerHTML = '<tr><td colspan="8" class="loading">Loading catalog...</td></tr>';

  const params = new URLSearchParams();
  if (q)        params.set('query', q);
  if (category) params.set('category', category);
  const r = await apiGet('/api/books?' + params.toString());
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Failed to load books', 'error');
    tbody.innerHTML = '<tr><td colspan="8"><div class="empty-state"><h3>Failed to load books</h3></div></td></tr>';
    return;
  }

  const books = r.data.data || [];
  if (books.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8">
      <div class="empty-state"><div class="empty-state-icon">📚</div>
      <h3>No books found</h3><p>Try adjusting your search query or category filter.</p></div></td></tr>`;
    return;
  }
  tbody.innerHTML = books.map(b => `
    <tr>
      <td><strong>${escapeHtml(b.title)}</strong></td>
      <td>${escapeHtml(b.author)}</td>
      <td><span class="font-mono text-xs">${escapeHtml(b.isbn)}</span></td>
      <td><span class="badge" style="background:#FFFFFF;border-color:#000;">${escapeHtml(b.category)}</span></td>
      <td><span class="badge badge-${b.type.toLowerCase()}">${escapeHtml(b.type)}</span></td>
      <td>${availBadge(b)}</td>
      <td><span class="font-mono text-xs font-bold">${b.loanPeriodDays}d · ₹${b.finePerDay}/day</span></td>
      <td style="white-space:nowrap;">
        <button class="neo-btn btn-white btn-sm" onclick="openEdit(${b.id})">Edit</button>
        <button class="neo-btn btn-pink btn-sm" style="margin-left:6px;" onclick="deleteBook(${b.id}, '${escapeHtml(b.title).replace(/'/g, "\\'")}')">Delete</button>
      </td>
    </tr>`).join('');
}

function availBadge(b) {
  if (b.availableCopies === 0) {
    return `<span class="badge badge-unavail">0/${b.totalCopies} Out</span>`;
  }
  if (b.availableCopies <= 1) {
    return `<span class="badge badge-low">${b.availableCopies}/${b.totalCopies} Low</span>`;
  }
  return `<span class="badge badge-available">${b.availableCopies}/${b.totalCopies} Avail</span>`;
}

function openAdd() {
  editingBookId = null;
  document.getElementById('modal-title').textContent = 'Add New Book';
  document.getElementById('book-form').reset();
  openModal('book-modal');
}

async function openEdit(id) {
  editingBookId = id;
  const r = await apiGet('/api/books/' + id);
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Error fetching book details', 'error');
    return;
  }
  const b = r.data.data;
  document.getElementById('modal-title').textContent = 'Edit Book Details';
  document.getElementById('f-title').value       = b.title;
  document.getElementById('f-author').value      = b.author;
  document.getElementById('f-isbn').value        = b.isbn;
  document.getElementById('f-category').value    = b.category;
  document.getElementById('f-type').value        = b.type;
  document.getElementById('f-copies').value      = b.totalCopies;
  openModal('book-modal');
}

async function saveBook(e) {
  e.preventDefault();
  const params = {
    title:       document.getElementById('f-title').value.trim(),
    author:      document.getElementById('f-author').value.trim(),
    isbn:        document.getElementById('f-isbn').value.trim(),
    category:    document.getElementById('f-category').value.trim(),
    type:        document.getElementById('f-type').value,
    totalCopies: document.getElementById('f-copies').value.trim(),
  };

  const r = editingBookId
    ? await apiPut('/api/books/' + editingBookId, params)
    : await apiPost('/api/books', params);

  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Error saving book', 'error');
    return;
  }
  showToast(r.data.message || 'Book saved successfully', 'success');
  closeModal('book-modal');
  loadBooks();
}

async function deleteBook(id, title) {
  if (!confirm(`Are you sure you want to delete "${title}"?`)) return;
  const r = await apiDelete('/api/books/' + id);
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Error deleting book', 'error');
    return;
  }
  showToast('Book deleted successfully.', 'success');
  loadBooks();
}

document.addEventListener('DOMContentLoaded', () => {
  loadBooks();
  document.getElementById('search-query').addEventListener('input', loadBooks);
  document.getElementById('search-category').addEventListener('change', loadBooks);
  document.getElementById('book-form').addEventListener('submit', saveBook);

  if (new URLSearchParams(window.location.search).get('action') === 'add') {
    openAdd();
  }
});
