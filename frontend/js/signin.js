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
        console.log('Attempting signin with username:', username);
        const response = await fetch(`${API_BASE_URL}/api/users/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        console.log('Signin response status:', response.status);

        if (response.ok) {
            const user = await response.json();
            console.log('Signin successful, user:', user);
            sessionStorage.setItem('user', JSON.stringify(user));
            window.location.href = '/index.html';
        } else {
            const contentType = response.headers.get('content-type');
            let errorMessage = 'Sign in failed. Invalid credentials.';
            
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
            
            alert('Sign in failed: ' + errorMessage);
            console.error('Sign in failed:', response.status, errorMessage);
        }
    } catch (error) {
        alert('Error: ' + error.message);
        console.error('Sign in error:', error);
    }
});
