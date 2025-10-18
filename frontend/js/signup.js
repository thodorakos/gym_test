document.getElementById('signup-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email-address').value;
    const phone = document.getElementById('phone').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/api/users/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, email, phone, password })
    });

    if (response.ok) {
        const user = await response.json();
        sessionStorage.setItem('user', JSON.stringify(user));
        window.location.href = '/';
    } else {
        // Handle error
        alert('Sign up failed. Please try again.');
        console.error('Sign up failed');
    }
});
