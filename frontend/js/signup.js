document.getElementById('signup-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email-address').value;
    const phone = document.getElementById('phone').value;
    const password = document.getElementById('password').value;

    // Validate that all fields are filled
    if (!username || !email || !phone || !password) {
        alert('Please fill in all fields.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/users/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, phone, password })
        });

        if (response.ok) {
            const user = await response.json();
            sessionStorage.setItem('user', JSON.stringify(user));
            window.location.href = '/index.html';
        } else {
            const errorData = await response.json();
            alert('Sign up failed: ' + (errorData.message || 'Please try again.'));
            console.error('Sign up failed:', errorData);
        }
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Sign up error:', error);
    }
});
