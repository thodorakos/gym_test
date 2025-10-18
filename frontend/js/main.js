document.addEventListener('DOMContentLoaded', () => {
    // Mobile menu
    const mobileMenuButton = document.querySelector('.mobile-menu-button');
    const mobileMenu = document.querySelector('.mobile-menu');

    if (mobileMenuButton) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

    const user = JSON.parse(sessionStorage.getItem('user'));
    const authLinks = document.getElementById('auth-links');
    const mobileAuthLinks = document.querySelector('.mobile-menu');

    if (user) {
        // User is signed in
        authLinks.innerHTML = `
            <a href="#" id="signout-btn" class="py-5 px-3 text-gray-700 hover:text-gray-900">Sign Out</a>
        `;
        if (user.role === 'ADMIN') {
            const adminLink = document.getElementById('admin-link');
            if(adminLink) adminLink.classList.remove('hidden');
        }
        const signoutBtn = document.getElementById('signout-btn');
        if(signoutBtn) {
            signoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                sessionStorage.removeItem('user');
                window.location.href = '/';
            });
        }
    } else {
        // User is not signed in
        if(authLinks){
            authLinks.innerHTML = `
                <a href="/signin.html" class="py-2 px-4">Sign In</a>
                <a href="/signup.html" class="py-2 px-4 bg-green-500 text-white rounded hover:bg-green-600">Sign Up</a>
            `;
        }
    }
});
