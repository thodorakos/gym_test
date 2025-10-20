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

        console.log('Booking session with data:', { sessionDate, sessionType, userId: user.id });

        try {
            const response = await fetch(`${API_BASE_URL}/api/sessions/book`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ sessionDate, sessionType, user: { id: user.id } })
            });

            console.log('Book session response status:', response.status);

            if (response.ok) {
                console.log('Session booked successfully');
                loadSessions();
                bookSessionForm.reset();
            } else {
                const errorData = await response.text();
                console.error('Failed to book session:', response.status, errorData);
                alert('Failed to book session: ' + errorData);
            }
        } catch (error) {
            console.error('Error booking session:', error);
            alert('Error booking session: ' + error.message);
        }
    });

    async function loadSessions() {
        try {
            console.log('Loading sessions for user:', user.id);
            const response = await fetch(`${API_BASE_URL}/api/sessions/user/${user.id}`);
            
            if (!response.ok) {
                console.error('Failed to load sessions:', response.status);
                return;
            }

            const sessions = await response.json();
            console.log('Sessions loaded:', sessions);

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
        } catch (error) {
            console.error('Error loading sessions:', error);
        }
    }

    loadSessions();
});
