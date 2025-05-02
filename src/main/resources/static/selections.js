const BTNS = document.querySelectorAll('.btn-custom');
const adminPanel = document.getElementById('adminPanel');
const userInfo = document.getElementById('userInfo');

const navLinks = document.querySelectorAll('.nav-link');
const usersList = document.getElementById('usersList');
const addUserForm = document.getElementById('addUserForm');

function removeStyles() {
    BTNS.forEach(el => {
        el.classList.remove('bg-primary');
        el.classList.remove('text-white');

        el.classList.add('bg-white');
        el.classList.add('text-primary');
    })
}

function removeActive() {
    navLinks.forEach(el => {
        el.classList.remove('active')
        el.classList.remove('text-primary');
        el.classList.add('text-black');

    })
}

BTNS.forEach(el => {
    el.addEventListener('click', () => {
        removeStyles();

        el.classList.remove('bg-white');
        el.classList.remove('text-primary');

        el.classList.add('bg-primary');
        el.classList.add('text-white');

        if (el.innerHTML == 'Admin') {
            adminPanel.style.display = 'block';
            userInfo.style.display = 'none';
        }

        if (el.innerHTML == 'User') {
            adminPanel.style.display = 'none';
            userInfo.style.display = 'block';
        }
    })
})

navLinks.forEach(el => {
    el.addEventListener('click', ()=> {
        removeActive();
        el.classList.add('active');
        el.classList.remove('text-black');
        el.classList.add('text-primary');

        if (el.innerHTML == 'Users table') {
            usersList.style.display = 'block';
            addUserForm.style.display = 'none';
        }

        if (el.innerHTML == 'New User') {
            usersList.style.display = 'none';
            addUserForm.style.display = 'block';
        }
    })
})