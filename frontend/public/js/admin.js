document.addEventListener('DOMContentLoaded', () => {
    const adminSessionsList = document.getElementById('admin-sessions-list');
    const user = JSON.parse(sessionStorage.getItem('user'));
    const signoutBtn = document.getElementById('signout-btn');
    const nameEl = document.getElementById('admin-name');

    // Check if user is admin
    if (!user || user.role !== 'ADMIN') {
        window.location.href = '/signin.html';
        return;
    }

    if (nameEl) nameEl.textContent = user.username;

    if (signoutBtn) {
        signoutBtn.addEventListener('click', () => {
            sessionStorage.removeItem('user');
            window.location.href = '/';
        });
    }

    async function loadAllSessions() {
        try {
            const response = await fetch(`${API_BASE_URL}/api/sessions/all`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const sessions = await response.json();

            // Clear existing content
            adminSessionsList.innerHTML = `
                <div class="grid grid-cols-5 gap-4 font-bold border-b pb-4 mb-4 text-gray-700">
                    <div>User</div>
                    <div>Email</div>
                    <div>Phone</div>
                    <div>Date & Time</div>
                    <div>Session Type</div>
                </div>
            `;

            if (sessions.length === 0) {
                const emptyMessage = document.createElement('div');
                emptyMessage.className = 'text-center py-8 text-gray-500';
                emptyMessage.textContent = 'No sessions have been booked yet.';
                adminSessionsList.appendChild(emptyMessage);
                return;
            }

            sessions.forEach(session => {
                if (session.user) {
                    const sessionElement = document.createElement('div');
                    sessionElement.className = 'grid grid-cols-5 gap-4 border-b py-4 hover:bg-gray-50';
                    
                    const sessionDate = new Date(session.sessionDate);
                    const formattedDate = sessionDate.toLocaleString('en-US', {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                    });

                    sessionElement.innerHTML = `
                        <div class="text-gray-800">${session.user.username}</div>
                        <div class="text-gray-600">${session.user.email}</div>
                        <div class="text-gray-600">${session.user.phone || 'N/A'}</div>
                        <div class="text-gray-800">${formattedDate}</div>
                        <div class="text-gray-800"><span class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm">${session.sessionType}</span></div>
                    `;
                    adminSessionsList.appendChild(sessionElement);
                }
            });
        } catch (error) {
            console.error('Failed to load sessions:', error);
            adminSessionsList.innerHTML = '<div class="text-center py-8 text-red-500">Failed to load sessions. Please try again later.</div>';
        }
    }

    loadAllSessions();
});
