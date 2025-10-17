// Mobile menu
const mobileMenuButton = document.querySelector('.mobile-menu-button');
const mobileMenu = document.querySelector('.mobile-menu');

mobileMenuButton.addEventListener('click', () => {
    mobileMenu.classList.toggle('hidden');
});

document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(sessionStorage.getItem('user'));
    if (!user) {
        window.location.href = 'signin.html';
    }

    // Show admin link if user is admin
    const adminLink = document.getElementById('admin-link');
    if (adminLink && user && user.role === 'ADMIN') {
        adminLink.classList.remove('hidden');
    }

    const signoutBtn = document.getElementById('signout-btn');
    if (signoutBtn) {
        signoutBtn.addEventListener('click', () => {
            sessionStorage.removeItem('user');
            window.location.href = '/';
        });
    }
});
