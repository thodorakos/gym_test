document.getElementById('signin-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Validate that all fields are filled
    if (!username || !password) {
        alert('Please fill in all fields.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/users/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const user = await response.json();
            sessionStorage.setItem('user', JSON.stringify(user));
            window.location.href = '/index.html';
        } else {
            const errorData = await response.text();
            alert('Sign in failed: ' + (errorData || 'Invalid credentials.'));
            console.error('Sign in failed:', errorData);
        }
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Sign in error:', error);
    }
});
