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
        console.log('Attempting signup with:', { username, email, phone });
        const response = await fetch(`${API_BASE_URL}/api/users/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, phone, password })
        });

        console.log('Signup response status:', response.status);

        if (response.ok) {
            const user = await response.json();
            console.log('Signup successful, user:', user);
            sessionStorage.setItem('user', JSON.stringify(user));
            window.location.href = '/index.html';
        } else {
            const contentType = response.headers.get('content-type');
            let errorMessage = 'Sign up failed. Please try again.';
            
            if (contentType && contentType.includes('application/json')) {
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || JSON.stringify(errorData);
                } catch (e) {
                    const errorText = await response.text();
                    errorMessage = errorText || errorMessage;
                }
            } else {
                errorMessage = await response.text() || errorMessage;
            }
            
            alert('Sign up failed: ' + errorMessage);
            console.error('Sign up failed:', response.status, errorMessage);
        }
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Sign up error:', error);
    }
});
