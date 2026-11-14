// students.js

async function loadStudents() {
  const q     = document.getElementById('search-query').value.trim();
  const tbody = document.getElementById('students-tbody');
  tbody.innerHTML = '<tr><td colspan="5" class="loading">Loading...</td></tr>';

  const params = new URLSearchParams();
  if (q) params.set('query', q);
  const r = await apiGet('/api/students?' + params.toString());
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Error loading students', 'error');
    tbody.innerHTML = '<tr><td colspan="5"><div class="empty-state"><h3>Unable to load students</h3></div></td></tr>';
    return;
  }

  const students = r.data.data || [];
  if (students.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state">
      <div class="empty-state-icon">👤</div>
      <h3>No students found</h3><p>Try searching by name, roll number, or department.</p></div></td></tr>`;
    return;
  }
  tbody.innerHTML = students.map(s => `
    <tr>
      <td><strong>${escapeHtml(s.name)}</strong></td>
      <td>${escapeHtml(s.email)}</td>
      <td>${escapeHtml(s.phone)}</td>
      <td><strong>${escapeHtml(s.rollNo)}</strong></td>
      <td><span class="badge badge-textbook">${escapeHtml(s.department)}</span></td>
    </tr>`).join('');
}

async function registerStudent(e) {
  e.preventDefault();
  const params = {
    name:       document.getElementById('f-name').value.trim(),
    email:      document.getElementById('f-email').value.trim(),
    phone:      document.getElementById('f-phone').value.trim(),
    rollNo:     document.getElementById('f-rollno').value.trim(),
    department: document.getElementById('f-dept').value.trim(),
  };

  const r = await apiPost('/api/students', params);
  if (!r.ok || !r.data.success) {
    showToast(r.data.message || 'Error registering student', 'error');
    return;
  }
  showToast(r.data.message || 'Student registered successfully', 'success');
  closeModal('student-modal');
  document.getElementById('student-form').reset();
  loadStudents();
}

document.addEventListener('DOMContentLoaded', () => {
  loadStudents();
  document.getElementById('search-query').addEventListener('input', loadStudents);
  document.getElementById('student-form').addEventListener('submit', registerStudent);
});
