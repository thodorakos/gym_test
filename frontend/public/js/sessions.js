document.addEventListener('DOMContentLoaded', () => {
    const bookSessionForm = document.getElementById('book-session-form');
    const sessionsList = document.getElementById('sessions-list');
    const user = JSON.parse(sessionStorage.getItem('user'));
    const signoutBtn = document.getElementById('signout-btn');

    if (!user) {
        window.location.href = '/signin.html';
        return;
    }

    // Show admin link if user is admin
    const adminLink = document.getElementById('admin-link');
    if (adminLink && user && user.role === 'ADMIN') {
        adminLink.classList.remove('hidden');
    }

    if (signoutBtn) {
        signoutBtn.addEventListener('click', () => {
            sessionStorage.removeItem('user');
            window.location.href = '/';
        });
    }

    bookSessionForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const sessionDate = document.getElementById('session-date').value;
        const sessionType = document.getElementById('session-type').value;

        const response = await fetch('/api/sessions/book', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ sessionDate, sessionType, user: { id: user.id } })
        });

        if (response.ok) {
            loadSessions();
            bookSessionForm.reset();
        } else {
            console.error('Failed to book session');
        }
    });

    async function loadSessions() {
        const response = await fetch(`/api/sessions/user/${user.id}`);
        const sessions = await response.json();

        sessionsList.innerHTML = '';
        sessions.forEach(session => {
            const sessionElement = document.createElement('div');
            sessionElement.className = 'bg-white p-6 rounded-lg shadow-md';
            sessionElement.innerHTML = `
                <h3 class="text-xl font-bold mb-2">${session.sessionType}</h3>
                <p class="text-gray-600">${new Date(session.sessionDate).toLocaleString()}</p>
            `;
            sessionsList.appendChild(sessionElement);
        });
    }

    loadSessions();
});
